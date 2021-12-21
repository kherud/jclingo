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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.Symbol;

/**
 * Captures a symbolic atom and provides properties to inspect its state.
 */
public class SymbolicAtom {

    private final Pointer symbolicAtoms;
    private final long iterator;

    /**
     * Creates a symbolic atom object from a pointer to a native symbolic atom set
     * and an associated iterator pointing to the specific atom.
     *
     * You probably do not want to call this explicitly, rather use the functionality of {@link SymbolicAtoms}
     *
     * @param symbolicAtoms pointer to the native symbolic atom set
     * @param iterator native iterator pointing to the atom
     */
    public SymbolicAtom(Pointer symbolicAtoms, long iterator) {
        this.symbolicAtoms = symbolicAtoms;
        this.iterator = iterator;
    }

    /**
     * @return Whether the atom is an external atom.
     */
    public boolean isExternal() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_is_external(symbolicAtoms, iterator, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @return Whether the atom is a fact.
     */
    public boolean isFact() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_is_fact(symbolicAtoms, iterator, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @return The program literal associated with the atom.
     */
    public int getLiteral() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_literal(symbolicAtoms, iterator, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return The representation of the atom in form of a symbol.
     */
    public Symbol getSymbol() {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_symbol(symbolicAtoms, iterator, longByReference));
        return Symbol.fromLong(longByReference.getValue());
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
