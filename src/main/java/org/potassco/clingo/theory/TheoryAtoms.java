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

package org.potassco.clingo.theory;

import java.util.Iterator;
import java.util.NoSuchElementException;

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

public class TheoryAtoms implements Iterable<TheoryAtom> {

    private final MemorySegment theoryAtoms;

    public TheoryAtoms(MemorySegment theoryAtoms) {
        this.theoryAtoms = theoryAtoms;
    }

    @Override
    public Iterator<TheoryAtom> iterator() {
        return new Iterator<>() {

            private final int size = size();
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public TheoryAtom next() {
                if (i >= size) {
                    throw new NoSuchElementException();
                }
                return new TheoryAtom(theoryAtoms, i++);
            }
        };
    }

    public TheoryAtom getTheoryAtom(int index) {
        return new TheoryAtom(theoryAtoms, index);
    }

    public int size() {
        return (int) Out.getLong(out -> ClingoH.clingo_theory_atoms_size(theoryAtoms, out));
    }
}
