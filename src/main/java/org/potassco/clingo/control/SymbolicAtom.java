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

package org.potassco.clingo.control;

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.Symbol;

/**
 * Captures a symbolic atom and provides properties to inspect its state.
 */
public class SymbolicAtom {

    private final MemorySegment symbolicAtoms;
    private final long iterator;

    /**
     * Creates a symbolic atom object from a pointer to a native symbolic atom set
     * and an associated iterator pointing to the specific atom.
     * <p>
     * You probably do not want to call this explicitly, rather use the functionality of {@link SymbolicAtoms}
     *
     * @param symbolicAtoms pointer to the native symbolic atom set
     * @param iterator native iterator pointing to the atom
     */
    public SymbolicAtom(MemorySegment symbolicAtoms, long iterator) {
        this.symbolicAtoms = symbolicAtoms;
        this.iterator = iterator;
    }

    /**
     * @return Whether the atom is an external atom.
     */
    public boolean isExternal() {
        return Out.getBoolean(out -> ClingoH.clingo_symbolic_atoms_is_external(symbolicAtoms, iterator, out));
    }

    /**
     * @return Whether the atom is a fact.
     */
    public boolean isFact() {
        return Out.getBoolean(out -> ClingoH.clingo_symbolic_atoms_is_fact(symbolicAtoms, iterator, out));
    }

    /**
     * @return The program literal associated with the atom.
     */
    public int getLiteral() {
        return Out.getInt(out -> ClingoH.clingo_symbolic_atoms_literal(symbolicAtoms, iterator, out));
    }

    /**
     * @return The representation of the atom in form of a symbol.
     */
    public Symbol getSymbol() {
        return Symbol.fromLong(Out.getLong(out ->
                ClingoH.clingo_symbolic_atoms_symbol(symbolicAtoms, iterator, out)));
    }

    @Override
    public String toString() {
        return getSymbol().toString();
    }

    /**
     * @param signature name, arity, and positivity of the function.
     * @return Whether the atom matches the signature.
     */
    public boolean match(Signature signature) {
        return getSymbol().match(signature);
    }
}
