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

import java.util.Arrays;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.symbol.Symbol;

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
     * Symbols that are not atoms of the program are false.
     *
     * @param symbols    array of symbols representing the clause
     * @param truthValue the truth value
     */
    public void addClause(Symbol[] symbols, TruthValue truthValue) {
        boolean negative = truthValue == TruthValue.FALSE;
        SymbolicAtoms symbolicAtoms = getSymbolicAtoms();
        int[] literals = new int[symbols.length];
        int size = 0;
        for (Symbol symbol : symbols) {
            if (symbolicAtoms.contains(symbol)) {
                int literal = symbolicAtoms.get(symbol).getLiteral();
                literals[size++] = negative ? -literal : literal;
            }
            // a negated unknown atom is true, which satisfies the clause, and a positive one is false, which drops out
            else if (negative) {
                return;
            }
        }
        addClause(Arrays.copyOf(literals, size));
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
        addClause(symbols, truthValue == TruthValue.FALSE ? TruthValue.TRUE : TruthValue.FALSE);
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
