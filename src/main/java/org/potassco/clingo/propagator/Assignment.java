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
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.solving.TruthValue;

import java.util.Iterator;

/**
 * Class to inspect the (parital) assignment of an associated solver.
 * <p>
 * Assigns truth values to solver literals.  Each solver literal is either
 * true, false, or undefined.
 * <p>
 * This class implements `Iterable[Integer]` to access the (positive)
 * literals in the assignment.
 */
public class Assignment implements Iterable<Integer> {

    private final Pointer assignment;

    public Assignment(Pointer assignment) {
        this.assignment = assignment;
    }

    /**
     * @return The number of (positive) literals in the assignment.
     */
    public int size() {
        return Clingo.INSTANCE.clingo_assignment_size(assignment).intValue();
    }

    /**
     * @param index the index of the literal
     * @return The (positive) literal at the given offset in the assignment.
     */
    public int getAssignmentAt(int index) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_at(assignment, new NativeSize(index), intByReference));
        return intByReference.getValue();
    }

    /**
     * Determine the decision literal given a decision level.
     *
     * @return the decision literal of the given level.
     */
    public int getDecision(int level) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_decision(assignment, level, intByReference));
        return intByReference.getValue();
    }

    /**
     * Get the current decision level.
     *
     * @return the current decision level.
     */
    public int getDecisionLevel() {
        return Clingo.INSTANCE.clingo_assignment_decision_level(assignment);
    }

    /**
     * Get the current root level.
     * Decisions levels smaller or equal to the root level are not backtracked during solving.
     *
     * @return the decision level
     */
    public int getRootLevel() {
        return Clingo.INSTANCE.clingo_assignment_root_level(assignment);
    }

    /**
     * Check if the given assignment is conflicting.
     *
     * @return whether the assignment is conflicting
     */
    public boolean isConflicting() {
        return Clingo.INSTANCE.clingo_assignment_has_conflict(assignment) > 0;
    }

    /**
     * Check if the assignment is total, i.e. there are no free literal.
     *
     * @return whether the assignment is total
     */
    public boolean isTotal() {
        return Clingo.INSTANCE.clingo_assignment_is_total(assignment) > 0;
    }

    /**
     * Check if the given literal is part of a (partial) assignment.
     *
     * @param literal The solver literal.
     * @return a bool determining if the given literal is valid in this solver.
     */
    public boolean hasLiteral(int literal) {
        return Clingo.INSTANCE.clingo_assignment_has_literal(assignment, literal) > 0;
    }

    /**
     * Check if a literal has a fixed truth value.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is false.
     */
    public boolean isFalse(int literal) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_is_false(assignment, literal, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Check if a literal has a fixed truth value.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is assigned on the top level.
     */
    public boolean isFixed(int literal) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_is_fixed(assignment, literal, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Check if a literal is true.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is true.
     */
    public boolean isTrue(int literal) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_is_true(assignment, literal, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Determine the truth value of a given literal.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is assigned on the top level.
     */
    public boolean isFree(int literal) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_truth_value(assignment, literal, intByReference));
        return intByReference.getValue() == TruthValue.FREE.getValue();
    }

    /**
     * Determine the decision level of a given literal.
     *
     * @param literal the literal
     * @return the resulting level
     */
    public int getLevel(int literal) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_assignment_level(assignment, literal, intByReference));
        return intByReference.getValue();
    }

    /**
     * Return a class to access literals assigned by the solver in chronological order.
     *
     * @return The trail of assigned literals.
     */
    public Trail getTrail() {
        return new Trail(this);
    }

    public Pointer getPointer() {
        return assignment;
    }


    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {

            private final int size = size();
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size - 1;
            }

            @Override
            public Integer next() {
                return getAssignmentAt(i++);
            }
        };
    }
}
