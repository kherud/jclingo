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

/**
 * <code>TheoryTerm</code> objects represent theory terms.
 * <p>
 * Theory terms have a readable string representation, implement Python's rich
 * comparison operators, and can be used as dictionary keys.
 */
public class TheoryTerm implements Comparable<TheoryTerm> {

    private final MemorySegment theoryAtoms;
    private final int id;

    public TheoryTerm(MemorySegment theoryAtoms, int id) {
        this.theoryAtoms = theoryAtoms;
        this.id = id;
    }

    /**
     * @return The name of the term (for symbols and functions).
     */
    public String getName() {
        return Out.getString(out -> ClingoH.clingo_theory_atoms_term_name(theoryAtoms, id, out));
    }

    /**
     * @return The numeric representation of the term (for numbers).
     */
    public int getNumber() {
        return Out.getInt(out -> ClingoH.clingo_theory_atoms_term_number(theoryAtoms, id, out));
    }

    /**
     * @return The type of the theory term.
     */
    public TheoryTermType getType() {
        return TheoryTermType.fromValue(
                Out.getInt(out -> ClingoH.clingo_theory_atoms_term_type(theoryAtoms, id, out)));
   }

    /**
     * @return The arguments of the term (for functions, tuples, list, and sets).
     */
    public TheoryTerm[] getArguments() {
        int[] ids = Out.getInts((arguments, size) ->
                ClingoH.clingo_theory_atoms_term_arguments(theoryAtoms, id, arguments, size));
        TheoryTerm[] theoryTerms = new TheoryTerm[ids.length];
        for (int i = 0; i < ids.length; i++) {
            theoryTerms[i] = new TheoryTerm(theoryAtoms, ids[i]);
        }
        return theoryTerms;
    }

    /**
     * Two items are equal if they have the same id and belong to the same container.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        TheoryTerm that = (TheoryTerm) other;
        return id == that.id && theoryAtoms.equals(that.theoryAtoms);
    }

    /**
     * Orders items of the same container by id. Items of different containers have no meaningful order, so comparing
     * them is rejected.
     */
    @Override
    public int compareTo(TheoryTerm other) {
        if (!theoryAtoms.equals(other.theoryAtoms)) {
            throw new IllegalArgumentException("cannot compare items of different theory atom containers");
        }
        return Integer.compare(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theoryAtoms, id);
    }

    @Override
    public String toString() {
        return NativeStr.getSized(
                size -> ClingoH.clingo_theory_atoms_term_to_string_size(theoryAtoms, id, size),
                (buffer, size) -> ClingoH.clingo_theory_atoms_term_to_string(theoryAtoms, id, buffer, size));
    }

}
