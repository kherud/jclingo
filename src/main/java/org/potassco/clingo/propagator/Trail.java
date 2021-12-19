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

package org.potassco.clingo.propagator;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.potassco.clingo.internal.Clingo;

import java.util.Iterator;

/**
 * Class to access literals assigned by the solver in chronological order.
 * <p>
 * Literals in the trail are ordered by decision levels, where the first
 * literal with a larger level than the previous literals is a decision; the
 * following literals with same level are implied by this decision literal.
 * Each decision level up to and including the current decision level has a
 * valid offset in the trail.
 */
public class Trail implements Iterable<Integer> {

    private final Pointer assignment;

    public Trail(Assignment assignment) {
        this.assignment = assignment.getPointer();
    }

    public Trail(Pointer assignment) {
        this.assignment = assignment;
    }

    /**
     * Returns the number of literals in the trail, i.e., the number of assigned literals.
     *
     * @return the number of literals in the trail
     */
    public int size() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_trail_size(assignment, intByReference));
        return intByReference.getValue();
    }

    /**
     * Returns the offset of the decision literal with the given decision level in
     * the trail.
     * <p>
     * Literals in the trail are ordered by decision levels, where the first
     * literal with a larger level than the previous literals is a decision; the
     * following literals with same level are implied by this decision literal.
     * Each decision level up to and including the current decision level has a
     * valid offset in the trail.
     *
     * @param level The decision level.
     * @return the offset of the decision literal
     */
    public int begin(int level) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_trail_begin(assignment, level, intByReference));
        return intByReference.getValue();
    }

    /**
     * Returns the offset of the decision literal with the given decision level in
     * the trail.
     * <p>
     * Literals in the trail are ordered by decision levels, where the first
     * literal with a larger level than the previous literals is a decision; the
     * following literals with same level are implied by this decision literal.
     * Each decision level up to and including the current decision level has a
     * valid offset in the trail.
     *
     * @param level the decision level
     * @return the offset of the decision literal
     */
    public int end(int level) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_trail_end(assignment, level, intByReference));
        return intByReference.getValue();
    }

    public Iterator<Integer> iterator() {
        return iterator(0);
    }

    public Iterator<Integer> iterator(int begin) {
        return new Iterator<>() {
            private final IntByReference intByReference = new IntByReference();
            private final int size = size();
            private int i = begin;

            @Override
            public boolean hasNext() {
                return i < size - 1;
            }

            @Override
            public Integer next() {
                Clingo.INSTANCE.clingo_assignment_trail_at(assignment, i++, intByReference);
                return intByReference.getValue();
            }
        };
    }
}
