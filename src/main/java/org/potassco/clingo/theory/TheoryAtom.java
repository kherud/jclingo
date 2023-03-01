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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.util.NoSuchElementException;

/**
 * Class to represent theory atoms.
 * <p>
 * Theory atoms have a readable string representation, implement Python's rich
 * comparison operators, and can be used as dictionary keys.
 */
public class TheoryAtom implements Comparable<TheoryAtom> {

    private final Pointer theoryAtoms;
    private final int id;

    // TODO: abstract basic structure?
    public TheoryAtom(Pointer theoryAtoms, int id) {
        this.theoryAtoms = theoryAtoms;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TheoryAtom that = (TheoryAtom) o;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public int compareTo(TheoryAtom other) {
        return Integer.compare(this.hashCode(), other.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * @return The program literal associated with the atom
     */
    public int getLiteral() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_atom_literal(theoryAtoms, id, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return The guard of the atom or null if the atom has no guard
     */
    public TheoryGuard getGuard() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_atom_has_guard(theoryAtoms, id, byteByReference));

        if (byteByReference.getValue() == 0)
            throw new NoSuchElementException("theory atom has no guard");

        String[] stringByReference = new String[1];
        IntByReference intByReference = new IntByReference();

        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_atom_guard(theoryAtoms, id, stringByReference, intByReference));
        TheoryTerm theoryTerm = new TheoryTerm(theoryAtoms, intByReference.getValue());
        return new TheoryGuard(stringByReference[0], theoryTerm);
    }

    /**
     * @return The term of the atom
     */
    public TheoryTerm getTerm() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_atom_term(theoryAtoms, id, intByReference));
        return new TheoryTerm(theoryAtoms, intByReference.getValue());
    }

    /**
     * @return The elements of the atom
     */
    public TheoryElement[] getElements() {
        PointerByReference pointerByReference = new PointerByReference();
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_atom_elements(theoryAtoms, id, pointerByReference, nativeSizeByReference));
        int amountElements = (int) nativeSizeByReference.getValue();
        TheoryElement[] theoryElements = new TheoryElement[amountElements];

        if (amountElements > 0) {
            int[] theoryElementInts = pointerByReference.getValue().getIntArray(0, amountElements);
            for (int i = 0; i < amountElements; i++) {
                theoryElements[i] = new TheoryElement(theoryAtoms, theoryElementInts[i]);
            }
        }
        return theoryElements;
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_atom_to_string_size(theoryAtoms, id, nativeSizeByReference));
        int stringSize = (int) nativeSizeByReference.getValue();
        byte[] atomBytes = new byte[stringSize];
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_atom_to_string(theoryAtoms, id, atomBytes, new NativeSize(stringSize)));
        return Native.toString(atomBytes);
    }
}
