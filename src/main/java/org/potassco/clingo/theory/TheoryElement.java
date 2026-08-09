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

import java.util.Objects;

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.propagator.PropagateInit;

/**
 * Class to represent theory elements.
 * <p>
 * Theory elements have a readable string representation, implement Python's rich
 * comparison operators, and can be used as dictionary keys.
 */
public class TheoryElement implements Comparable<TheoryElement> {

    private final MemorySegment theoryAtoms;
    private final int id;

    public TheoryElement(MemorySegment theoryAtoms, int id) {
        this.theoryAtoms = theoryAtoms;
        this.id = id;
    }



    /**
     * Orders elements of the same container by id. Elements of different containers have no meaningful order, so
     * comparing them is rejected.
     */
    @Override
    public int compareTo(TheoryElement other) {
        if (!theoryAtoms.equals(other.theoryAtoms)) {
            throw new IllegalArgumentException("cannot compare elements of different theory atom containers");
        }
        return Integer.compare(id, other.id);
    }

    /**
     * Each condition has an id, which is a temporary program literal. This id
     * can be passed to {@link PropagateInit#solverLiteral(int)} to obtain a corresponding solver literal.
     *
     * @return condition literal
     */
    public int getConditionId() {
        return Out.getInt(out -> ClingoH.clingo_theory_atoms_element_condition_id(theoryAtoms, id, out));
    }

    /**
     * @return The tuple of the element
     */
    public TheoryTerm[] getTerms() {
        int[] ids = Out.getInts((terms, size) ->
                ClingoH.clingo_theory_atoms_element_tuple(theoryAtoms, id, terms, size));
        TheoryTerm[] theoryTerms = new TheoryTerm[ids.length];
        for (int i = 0; i < ids.length; i++) {
            theoryTerms[i] = new TheoryTerm(theoryAtoms, ids[i]);
        }
        return theoryTerms;
    }

    /**
     * @return The condition of the element in form of a list of program literals
     */
    public int[] getConditions() {
        // TODO: return something different than ints?
        return Out.getInts((condition, size) ->
                ClingoH.clingo_theory_atoms_element_condition(theoryAtoms, id, condition, size));
    }

    /**
     * Two elements are equal if they have the same id and belong to the same container.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        TheoryElement that = (TheoryElement) other;
        return id == that.id && theoryAtoms.equals(that.theoryAtoms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theoryAtoms, id);
    }

    @Override
    public String toString() {
        return NativeStr.getSized(
                size -> ClingoH.clingo_theory_atoms_element_to_string_size(theoryAtoms, id, size),
                (buffer, size) -> ClingoH.clingo_theory_atoms_element_to_string(theoryAtoms, id, buffer, size));
    }
}
