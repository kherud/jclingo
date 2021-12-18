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

import com.sun.jna.Pointer;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.util.Iterator;

public class TheoryAtoms implements Iterable<TheoryAtom> {

    private final Pointer theoryAtoms;

    public TheoryAtoms(Pointer theoryAtoms) {
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
                return new TheoryAtom(theoryAtoms, i++);
            }
        };
    }

    public TheoryAtom getTheoryAtom(int index) {
        return new TheoryAtom(theoryAtoms, index);
    }

    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_size(theoryAtoms, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }
}
