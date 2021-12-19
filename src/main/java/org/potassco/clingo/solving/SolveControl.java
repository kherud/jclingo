/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.potassco.clingo.solving;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.symbol.Symbol;

import java.util.NoSuchElementException;

/**
 * Object that allows for controlling a running search.
 */
public class SolveControl {

    private final Pointer solveControl;

    public SolveControl(Pointer solveControl) {
        this.solveControl = solveControl;
    }

    /**
     * Add a clause that applies to the current solving step during model
     * enumeration.
     * <p>
     * The {@link org.potassco.clingo.propagator.Propagator} module provides a more sophisticated
     * interface to add clauses - even on partial assignments.
     *
     * @param clause array of literals representing the clause
     */
    public void addClause(int[] clause) {
        Clingo.check(Clingo.INSTANCE.clingo_solve_control_add_clause(solveControl, clause, new NativeSize(clause.length)));
    }

    /**
     * Add a clause that applies to the current solving step during model
     * enumeration.
     * <p>
     * The {@link org.potassco.clingo.propagator.Propagator} module provides a more sophisticated
     * interface to add clauses - even on partial assignments.
     *
     * @param symbols    array of symbols representing the clause
     * @param truthValue the truth value
     */
    public void addClause(Symbol[] symbols, TruthValue truthValue) {
        SymbolicAtoms symbolicAtoms = getSymbolicAtoms();
        int[] literals = new int[symbols.length];
        for (int i = 0; i < literals.length; i++)
            try {
                literals[i] = symbolicAtoms.get(symbols[i]).getLiteral();
                literals[i] = truthValue == TruthValue.TRUE || truthValue == TruthValue.FREE ? literals[i] : -literals[i];
            } catch (NoSuchElementException e) {
                literals[i] = -1;
            }

        addClause(literals);
    }

    /**
     * Equivalent to {@link SolveControl#addClause(int[])} with the literals inverted.
     * <p>
     * * @param clause array of literals representing the clause
     */
    public void addNogood(int[] clause) {
        int[] negatedClause = new int[clause.length];
        for (int i = 0; i < clause.length; i++) {
            negatedClause[i] = -clause[i];
        }
        addClause(negatedClause);
    }

    /**
     * Equivalent to {@link SolveControl#addClause(Symbol[], TruthValue)} with inverted truth value inverted.
     *
     * @param symbols    array of symbols representing the clause
     * @param truthValue the truth value to invert
     */
    public void addNogood(Symbol[] symbols, TruthValue truthValue) {
        if (truthValue == TruthValue.TRUE)
            addClause(symbols, TruthValue.FALSE);
        else if (truthValue == TruthValue.FALSE)
            addClause(symbols, TruthValue.TRUE);
        else
            addNogood(symbols, TruthValue.FREE);
    }

    /**
     * @return object to inspect the symbolic atoms.
     */
    public SymbolicAtoms getSymbolicAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_solve_control_symbolic_atoms(solveControl, pointerByReference));
        return new SymbolicAtoms(pointerByReference.getValue());
    }
}
