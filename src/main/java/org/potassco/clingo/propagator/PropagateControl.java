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

/**
 * This object can be used to add clauses and to propagate them.
 */
public class PropagateControl {

    public final Pointer propagateControl;

    public PropagateControl(Pointer propagateControl) {
        this.propagateControl = propagateControl;
    }

    /**
     * Add the given clause to the solver.
     *
     * @param clause List of solver literals forming the clause.
     * @param tag If true, the clause applies only in the current solving step.
     * @param lock If true, exclude clause from the solver's regular clause deletion policy.
     * @return This method returns false if the current propagation must be stopped.
     */
    public boolean addClause(int[] clause, boolean tag, boolean lock) {
        ClauseType type = tag ? lock ? ClauseType.VOLATILE_STATIC : ClauseType.VOLATILE : ClauseType.LEARNT;
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_control_add_clause(
                propagateControl,
                clause,
                new NativeSize(clause.length),
                type.getValue(),
                byteByReference
        ));
        return byteByReference.getValue() > 0;
    }

    /**
     * Adds a new positive volatile literal to the underlying solver thread.
     *
     * The literal is only valid within the current solving step and solver
     * thread. All volatile literals and clauses involving a volatile literal
     * are deleted after the current search.
     *
     * @return The added solver literal.
     */
    public int addLiteral() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_control_add_literal(propagateControl, intByReference));
        return intByReference.getValue();
    }

    /**
     * Equivalent to `self.add_clause([-lit for lit in clause], tag, lock)`.
     *
     * @param clause List of solver literals forming the clause.
     * @param tag If true, the clause applies only in the current solving step.
     * @param lock If true, exclude clause from the solver's regular clause deletion policy.
     * @return This method returns false if the current propagation must be stopped.
     */
    public boolean addNoGood(int[] clause, boolean tag, boolean lock) {
        int[] negatedClause = new int[clause.length];
        for (int i = 0; i < clause.length; i++) {
            negatedClause[i] = -clause[i];
        }
        return addClause(negatedClause, tag, lock);
    }

    /**
     * Add a watch for the solver literal in the given phase.
     *
     * Unlike `PropagateInit.add_watch` this does not add a watch to all
     * solver threads but just the current one.
     *
     * @param literal The target solver literal.
     */
    public void addWatch(int literal) {
        Clingo.check(Clingo.INSTANCE.clingo_propagate_control_add_watch(propagateControl, literal));
    }

    /**
     * Check whether a literal is watched in the current solver thread.
     *
     * @param literal The target solver literal.
     * @return Whether the literal is watched.
     */
    public boolean hasWatch(int literal) {
        return Clingo.INSTANCE.clingo_propagate_control_has_watch(propagateControl, literal) > 0;
    }

    /**
     * Propagate literals implied by added clauses.
     *
     * @return This method returns false if the current propagation must be stopped.
     */
    public boolean propagate() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_control_propagate(propagateControl, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Removes the watch (if any) for the given solver literal.
     *
     * @param literal The target solver literal.
     */
    public void removeWatch(int literal) {
        Clingo.INSTANCE.clingo_propagate_control_remove_watch(propagateControl, literal);
    }

    /**
     * @return `Assignment` object capturing the partial assignment of the current solver thread.
     */
    public Assignment getAssignment() {
        Pointer assignment = Clingo.INSTANCE.clingo_propagate_control_assignment(propagateControl);
        return new Assignment(assignment);
    }

    /**
     * @return The numeric id of the current solver thread.
     */
    public int getThreadId() {
        return Clingo.INSTANCE.clingo_propagate_control_thread_id(propagateControl);
    }
}
