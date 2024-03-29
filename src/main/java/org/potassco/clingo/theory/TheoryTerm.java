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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

/**
 * <code>TheoryTerm</code> objects represent theory terms.
 * <p>
 * Theory terms have a readable string representation, implement Python's rich
 * comparison operators, and can be used as dictionary keys.
 */
public class TheoryTerm implements Comparable<TheoryTerm> {

    private final Pointer theoryAtoms;
    private final int id;

    public TheoryTerm(Pointer theoryAtoms, int id) {
        this.theoryAtoms = theoryAtoms;
        this.id = id;
    }

    /**
     * @return The name of the term (for symbols and functions).
     */
    public String getName() {
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_term_name(theoryAtoms, id, stringByReference));
        return stringByReference[0];
    }

    /**
     * @return The numeric representation of the term (for numbers).
     */
    public int getNumber() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_term_number(theoryAtoms, id, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return The type of the theory term.
     */
    public TheoryTermType getType() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_term_type(theoryAtoms, id, intByReference));
        return TheoryTermType.fromValue(intByReference.getValue());
   }

    /**
     * @return The arguments of the term (for functions, tuples, list, and sets).
     */
    public TheoryTerm[] getArguments() {
        PointerByReference pointerByReference = new PointerByReference();
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_term_arguments(theoryAtoms, id, pointerByReference, nativeSizeByReference));
        int amountElements = (int) nativeSizeByReference.getValue();
        TheoryTerm[] theoryTerms = new TheoryTerm[amountElements];

        if (amountElements > 0) {
            int[] theoryElementInts = pointerByReference.getValue().getIntArray(0, amountElements);
            for (int i = 0; i < amountElements; i++) {
                theoryTerms[i] = new TheoryTerm(theoryAtoms, theoryElementInts[i]);
            }
        }
        return theoryTerms;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TheoryTerm))
            return false;
        return this.hashCode() == other.hashCode();
    }

    @Override
    public int compareTo(TheoryTerm other) {
        return Integer.compare(this.hashCode(), other.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_term_to_string_size(theoryAtoms, id, nativeSizeByReference));
        int stringSize = (int) nativeSizeByReference.getValue();
        byte[] termBytes = new byte[stringSize];
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_term_to_string(theoryAtoms, id, termBytes, new NativeSize(stringSize)));
        return Native.toString(termBytes);
    }

}
