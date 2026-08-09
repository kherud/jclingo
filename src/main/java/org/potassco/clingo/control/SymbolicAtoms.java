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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.Symbol;

/**
 * This class provides read-only access to the atom base of the grounder.
 */
public class SymbolicAtoms implements Iterable<SymbolicAtom> {

    private final MemorySegment symbolicAtoms;

    public SymbolicAtoms(MemorySegment symbolicAtoms) {
        this.symbolicAtoms = symbolicAtoms;
    }

    /**
     * @param symbol the symbol to return the symbolic atom for
     * @return the symbolic atom of the symbol
     */
    public SymbolicAtom get(Symbol symbol) {
        long iterator = find(symbol);
        if (!isValid(iterator)) {
            throw new NoSuchElementException("Symbol '" + symbol + "' has no symbolic atom");
        }
        return new SymbolicAtom(symbolicAtoms, iterator);
    }

    /**
     * @return all symbolic atoms in the program
     */
    public List<SymbolicAtom> getAll() {
        List<SymbolicAtom> symbolicAtoms = new ArrayList<>();
        iterator().forEachRemaining(symbolicAtoms::add);
        return symbolicAtoms;
    }

    /**
     * @param symbol the symbol to check for if a symbolic atom exists
     * @return a boolean indicating whether the symbolic atom exists
     */
    public boolean contains(Symbol symbol) {
        return isValid(find(symbol));
    }

    private long find(Symbol symbol) {
        return Out.getLong(out -> ClingoH.clingo_symbolic_atoms_find(symbolicAtoms, symbol.getLong(), out));
    }

    private boolean isValid(long iterator) {
        return Out.getBoolean(out -> ClingoH.clingo_symbolic_atoms_is_valid(symbolicAtoms, iterator, out));
    }

    /**
     * @return The amount of symbolic atoms in the program.
     */
    public int size() {
        return (int) Out.getLong(out -> ClingoH.clingo_symbolic_atoms_size(symbolicAtoms, out));
    }

    /**
     * @return The number of different predicate signatures used in the program.
     */
    public int amountSignatures() {
        return (int) Out.getLong(out -> ClingoH.clingo_symbolic_atoms_signatures_size(symbolicAtoms, out));
    }

    /**
     * @return The list of predicate signatures occurring in the program.
     */
    public List<Signature> getSignatures() {
        try (Arena call = Arena.ofConfined()) {
            long size = amountSignatures();
            MemorySegment buffer = call.allocate(ValueLayout.JAVA_LONG, size);
            Clingo.check(ClingoH.clingo_symbolic_atoms_signatures(symbolicAtoms, buffer, size));
            List<Signature> signatures = new ArrayList<>();
            for (long signature : buffer.toArray(ValueLayout.JAVA_LONG)) {
                signatures.add(new Signature(signature));
            }
            return signatures;
        }
    }

    /**
     * @return an iterator over all symbolic atoms in the program
     */
    @Override
    public Iterator<SymbolicAtom> iterator() {
        return iterator(nativeIterator());
    }

    /**
     * @return an iterator over all symbolic atoms in the program matching the signature
     */
    public Iterator<SymbolicAtom> iterator(Signature signature) {
        long nativeIterator = nativeIteratorBySignature(signature.getLong());
        return iterator(nativeIterator);
    }

    private long nativeIteratorBySignature(long signature) {
        try (Arena call = Arena.ofConfined()) {
            MemorySegment signature$ = call.allocateFrom(ValueLayout.JAVA_LONG, signature);
            return Out.getLong(out -> ClingoH.clingo_symbolic_atoms_begin(symbolicAtoms, signature$, out));
        }
    }

    private long nativeIterator() {
        return Out.getLong(out -> ClingoH.clingo_symbolic_atoms_begin(symbolicAtoms, MemorySegment.NULL, out));
    }

    private Iterator<SymbolicAtom> iterator(long nativeIterator) {
        return new Iterator<>() {

            private long iterator = nativeIterator;

            @Override
            public boolean hasNext() {
                return isValid(iterator);
            }

            @Override
            public SymbolicAtom next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                long current = iterator;
                iterator = Out.getLong(out -> ClingoH.clingo_symbolic_atoms_next(symbolicAtoms, current, out));
                return new SymbolicAtom(symbolicAtoms, current);
            }
        };
    }
}
