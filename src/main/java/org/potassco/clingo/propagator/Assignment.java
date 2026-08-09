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

import java.util.Iterator;
import java.util.NoSuchElementException;

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.solving.TruthValue;

/**
 * Class to inspect the (partial) assignment of an associated solver.
 * <p>
 * Assigns truth values to solver literals.  Each solver literal is either
 * true, false, or undefined.
 * <p>
 * This class implements <code>Iterable&lt;Integer&gt;</code> to access the (positive)
 * literals in the assignment.
 */
public class Assignment implements Iterable<Integer> {

    private final MemorySegment assignment;

    public Assignment(MemorySegment assignment) {
        this.assignment = assignment;
    }

    /**
     * @return The number of (positive) literals in the assignment.
     */
    public int size() {
        return (int) ClingoH.clingo_assignment_size(assignment);
    }

    /**
     * @param index the index of the literal
     * @return The (positive) literal at the given offset in the assignment.
     */
    public int get(int index) {
        return Out.getInt(out -> ClingoH.clingo_assignment_at(assignment, index, out));
    }

    /**
     * @param begin the start index of the literal
     * @param end   the end index of the literal
     * @return The (positive) literals at the given interval in the assignment.
     */
    public int[] get(int begin, int end) {
        assert end > begin;
        int[] literals = new int[end - begin];
        for (int i = begin; i < end; i++) {
            literals[i - begin] = get(i);
        }
        return literals;
    }

    /**
     * Determine the truth value of a given literal.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is assigned on the top level.
     */
    public TruthValue getTruthValue(int literal) {
        return TruthValue.fromValue(Out.getInt(out -> ClingoH.clingo_assignment_truth_value(assignment, literal, out)));
    }

    /**
     * Determine the decision literal given a decision level.
     *
     * @return the decision literal of the given level.
     */
    public int getDecision(int level) {
        return Out.getInt(out -> ClingoH.clingo_assignment_decision(assignment, level, out));
    }

    /**
     * Get the current decision level.
     *
     * @return the current decision level.
     */
    public int getDecisionLevel() {
        return ClingoH.clingo_assignment_decision_level(assignment);
    }

    /**
     * Get the current root level.
     * Decisions levels smaller or equal to the root level are not backtracked during solving.
     *
     * @return the decision level
     */
    public int getRootLevel() {
        return ClingoH.clingo_assignment_root_level(assignment);
    }

    /**
     * Check if the given assignment is conflicting.
     *
     * @return whether the assignment is conflicting
     */
    public boolean isConflicting() {
        return ClingoH.clingo_assignment_has_conflict(assignment);
    }

    /**
     * Check if the assignment is total, i.e. there are no free literal.
     *
     * @return whether the assignment is total
     */
    public boolean isTotal() {
        return ClingoH.clingo_assignment_is_total(assignment);
    }

    /**
     * Check if the given literal is part of a (partial) assignment.
     *
     * @param literal The solver literal.
     * @return a bool determining if the given literal is valid in this solver.
     */
    public boolean hasLiteral(int literal) {
        return ClingoH.clingo_assignment_has_literal(assignment, literal);
    }

    /**
     * Check if a literal has a fixed truth value.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is false.
     */
    public boolean isFalse(int literal) {
        return Out.getBoolean(out -> ClingoH.clingo_assignment_is_false(assignment, literal, out));
    }

    /**
     * Check if a literal has a fixed truth value.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is assigned on the top level.
     */
    public boolean isFixed(int literal) {
        return Out.getBoolean(out -> ClingoH.clingo_assignment_is_fixed(assignment, literal, out));
    }

    /**
     * Check if a literal is true.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is true.
     */
    public boolean isTrue(int literal) {
        return Out.getBoolean(out -> ClingoH.clingo_assignment_is_true(assignment, literal, out));
    }

    /**
     * Determine if the literal is free.
     *
     * @param literal The solver literal.
     * @return a bool determining if the literal is assigned on the top level.
     */
    public boolean isFree(int literal) {
        return getTruthValue(literal) == TruthValue.FREE;
    }

    /**
     * Determine the decision level of a given literal.
     *
     * @param literal the literal
     * @return the resulting level
     */
    public int getLevel(int literal) {
        return Out.getInt(out -> ClingoH.clingo_assignment_level(assignment, literal, out));
    }

    /**
     * Return a class to access literals assigned by the solver in chronological order.
     *
     * @return The trail of assigned literals.
     */
    public Trail getTrail() {
        return new Trail(this);
    }

    public MemorySegment getPointer() {
        return assignment;
    }


    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {

            private final int size = size();
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Integer next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return get(i++);
            }
        };
    }
}
