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

import java.util.NoSuchElementException;
import java.util.Objects;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

/**
 * Class to represent theory atoms.
 * <p>
 * Theory atoms have a readable string representation, implement Python's rich
 * comparison operators, and can be used as dictionary keys.
 */
public class TheoryAtom implements Comparable<TheoryAtom> {

    private final MemorySegment theoryAtoms;
    private final int id;

    // TODO: abstract basic structure?
    public TheoryAtom(MemorySegment theoryAtoms, int id) {
        this.theoryAtoms = theoryAtoms;
        this.id = id;
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
        TheoryAtom that = (TheoryAtom) other;
        return id == that.id && theoryAtoms.equals(that.theoryAtoms);
    }

    /**
     * Orders items of the same container by id. Items of different containers have no meaningful order, so comparing
     * them is rejected.
     */
    @Override
    public int compareTo(TheoryAtom other) {
        if (!theoryAtoms.equals(other.theoryAtoms)) {
            throw new IllegalArgumentException("cannot compare items of different theory atom containers");
        }
        return Integer.compare(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theoryAtoms, id);
    }

    /**
     * @return The program literal associated with the atom
     */
    public int getLiteral() {
        return Out.getInt(out -> ClingoH.clingo_theory_atoms_atom_literal(theoryAtoms, id, out));
    }

    /**
     * @return whether the atom has a guard
     */
    public boolean hasGuard() {
        return Out.getBoolean(out -> ClingoH.clingo_theory_atoms_atom_has_guard(theoryAtoms, id, out));
    }

    /**
     * @return The guard of the atom
     * @throws NoSuchElementException if the atom has no guard
     */
    public TheoryGuard getGuard() {
        if (!hasGuard()) {
            throw new NoSuchElementException("theory atom has no guard");
        }

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment connective = arena.allocate(ValueLayout.ADDRESS);
            MemorySegment term = arena.allocate(ValueLayout.JAVA_INT);
            Clingo.check(ClingoH.clingo_theory_atoms_atom_guard(theoryAtoms, id, connective, term));
            return new TheoryGuard(NativeStr.toJava(connective.get(ValueLayout.ADDRESS, 0)),
                    new TheoryTerm(theoryAtoms, term.get(ValueLayout.JAVA_INT, 0)));
        }
    }

    /**
     * @return The term of the atom
     */
    public TheoryTerm getTerm() {
        return new TheoryTerm(theoryAtoms,
                Out.getInt(out -> ClingoH.clingo_theory_atoms_atom_term(theoryAtoms, id, out)));
    }

    /**
     * @return The elements of the atom
     */
    public TheoryElement[] getElements() {
        int[] ids = Out.getInts((elements, size) ->
                ClingoH.clingo_theory_atoms_atom_elements(theoryAtoms, id, elements, size));
        TheoryElement[] theoryElements = new TheoryElement[ids.length];
        for (int i = 0; i < ids.length; i++) {
            theoryElements[i] = new TheoryElement(theoryAtoms, ids[i]);
        }
        return theoryElements;
    }

    @Override
    public String toString() {
        return NativeStr.getSized(
                size -> ClingoH.clingo_theory_atoms_atom_to_string_size(theoryAtoms, id, size),
                (buffer, size) -> ClingoH.clingo_theory_atoms_atom_to_string(theoryAtoms, id, buffer, size));
    }
}
