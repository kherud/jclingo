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
 * Class to represent theory elements.
 *
 * Theory elements have a readable string representation, implement Python's rich
 * comparison operators, and can be used as dictionary keys.
 */
public class TheoryElement implements Comparable<TheoryElement> {

    private final Pointer theoryAtoms;
    private final int id;

    public TheoryElement(Pointer theoryAtoms, int id) {
        this.theoryAtoms = theoryAtoms;
        this.id = id;
    }

    public boolean equals(TheoryElement other) {
        return this.hashCode() == other.hashCode();
    }

    public int compareTo(TheoryElement other) {
        return Integer.compare(this.hashCode(), other.hashCode());
    }

    /**
     * Each condition has an id, which is a temporary program literal. This id
     * can be passed to `clingo.propagator.PropagateInit.solver_literal` to
     * obtain a corresponding solver literal.
     * @return condition literal
     */
    public int getConditionId() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_element_condition_id(theoryAtoms, id, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return The tuple of the element
     */
    public TheoryTerm[] getTerms() {
        PointerByReference pointerByReference = new PointerByReference();
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_element_tuple(theoryAtoms, id, pointerByReference, nativeSizeByReference));
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

    /**
     * @return The condition of the element in form of a list of program literals
     */
    public int[] getConditions() {
        PointerByReference pointerByReference = new PointerByReference();
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_element_condition(theoryAtoms, id, pointerByReference, nativeSizeByReference));
        int amountElements = (int) nativeSizeByReference.getValue();
        return amountElements == 0 ? new int[0] : pointerByReference.getValue().getIntArray(0, amountElements);
        // TODO: return something different than ints?
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_element_to_string_size(theoryAtoms, id, nativeSizeByReference));
        int stringSize = (int) nativeSizeByReference.getValue();
        byte[] elementBytes = new byte[stringSize];
        Clingo.check(Clingo.INSTANCE.clingo_theory_atoms_element_to_string(theoryAtoms, id, elementBytes, new NativeSize(stringSize)));
        return Native.toString(elementBytes);
    }
}
