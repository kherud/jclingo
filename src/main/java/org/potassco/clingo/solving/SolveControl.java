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

import java.util.NoSuchElementException;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.symbol.Symbol;

/**
 * Object that allows for controlling a running search.
 */
public class SolveControl {

    private final MemorySegment solveControl;

    public SolveControl(MemorySegment solveControl) {
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
        try (Arena call = Arena.ofConfined()) {
            Clingo.check(ClingoH.clingo_solve_control_add_clause(solveControl,
                    call.allocateFrom(ValueLayout.JAVA_INT, clause), clause.length));
        }
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
     * @throws NoSuchElementException if one of the symbols is not an atom of the program
     */
    public void addClause(Symbol[] symbols, TruthValue truthValue) {
        SymbolicAtoms symbolicAtoms = getSymbolicAtoms();
        int[] literals = new int[symbols.length];
        for (int i = 0; i < literals.length; i++) {
            int literal = symbolicAtoms.get(symbols[i]).getLiteral();
            literals[i] = truthValue == TruthValue.FALSE ? -literal : literal;
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
        addClause(symbols, truthValue == TruthValue.FALSE ? TruthValue.TRUE : TruthValue.FALSE);
    }

    /**
     * @return object to inspect the symbolic atoms.
     */
    public SymbolicAtoms getSymbolicAtoms() {
        return new SymbolicAtoms(Out.getPointer(out ->
                ClingoH.clingo_solve_control_symbolic_atoms(solveControl, out)));
    }
}
