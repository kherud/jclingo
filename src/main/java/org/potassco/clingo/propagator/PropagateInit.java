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
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.theory.TheoryAtoms;

/**
 * Object that is used to initialize a propagator before each solving step.
 */
public class PropagateInit {

    private final Pointer propagateInit;

    public PropagateInit(Pointer propagateInit) {
        this.propagateInit = propagateInit;
    }

    /**
     * Statically adds the given clause to the problem.
     * If this function returns false, initialization should be stopped and no
     * further functions of the <code>PropagateInit</code> and related objects should be called.
     *
     * @param clause The clause over solver literals to add.
     * @return Returns false if the program becomes unsatisfiable.
     */
    public boolean addClause(int[] clause) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_add_clause(propagateInit, clause, new NativeSize(clause.length), byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Statically adds a literal to the solver.
     * <p>
     * To be able to use the variable in clauses during propagation or add
     * watches to it, it has to be frozen. Otherwise, it might be removed
     * during preprocessing.
     * <p>
     * If literals are added to the solver, subsequent calls to {@link #addClause(int[])} and
     * {@link #propagate()} are expensive. It is best to add literals in batches.
     *
     * @return Returns the added literal.
     */
    public int addLiteral() {
        return addLiteral(true);
    }

    /**
     * Statically adds a literal to the solver.
     * <p>
     * To be able to use the variable in clauses during propagation or add
     * watches to it, it has to be frozen. Otherwise, it might be removed
     * during preprocessing.
     * <p>
     * If literals are added to the solver, subsequent calls to {@link #addClause(int[])} and
     * {@link #propagate()} are expensive. It is best to add literals in batches.
     *
     * @param freeze Whether to freeze the variable.
     * @return Returns the added literal.
     */
    public int addLiteral(boolean freeze) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_add_literal(
                propagateInit,
                freeze ? (byte) 1 : 0,
                intByReference)
        );
        return intByReference.getValue();
    }

    /**
     * Extends the solver's minimize constraint with the given weighted literal.
     *
     * @param literal  The literal to add.
     * @param weight   The weight of the literal.
     */
    public void addMinimize(int literal, int weight) {
        addMinimize(literal, weight, 0);
    }

    /**
     * Extends the solver's minimize constraint with the given weighted literal.
     *
     * @param literal  The literal to add.
     * @param weight   The weight of the literal.
     * @param priority The priority of the literal.
     */
    public void addMinimize(int literal, int weight, int priority) {
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_add_minimize(propagateInit, literal, weight, priority));
    }

    /**
     * Add a watch for the solver literal in the given phase.
     * All active threads will watch the literal.
     *
     * @param literal The solver literal to watch.
     */
    public void addWatch(int literal) {
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_add_watch(propagateInit, literal));
    }

    /**
     * Add a watch to a specific thread for the solver literal in the given phase.
     *
     * @param literal  The solver literal to watch.
     * @param threadId The id of the thread to watch the literal.
     */
    public void addWatch(int literal, int threadId) {
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_add_watch_to_thread(propagateInit, literal, threadId));
    }

    /**
     * Remove the watch for the solver literal in the given phase.
     * The watch is removed from all active threads.
     *
     * @param literal The solver literal to remove the watch from.
     */
    public void removeWatch(int literal) {
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_remove_watch(propagateInit, literal));
    }

    /**
     * Remove the watch for the solver literal in the given phase.
     *
     * @param literal  The solver literal to remove the watch from.
     * @param threadId The id of the thread from which to remove the watch.
     */
    public void removeWatch(int literal, int threadId) {
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_remove_watch_from_thread(propagateInit, literal, threadId));
    }

    /**
     * Freeze the given solver literal.
     * <p>
     * Any solver literal that is not frozen is subject to simplification and
     * might be removed in a preprocessing step after propagator
     * initialization. A propagator should freeze all literals over which it
     * might add clauses during propagation. Note that any watched literal is
     * automatically frozen and that it does not matter which phase of the
     * literal is frozen.
     *
     * @param literal The solver literal to freeze.
     */
    public void freezeLiteral(int literal) {
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_freeze_literal(propagateInit, literal));
    }

    /**
     * Statically adds a constraint of form <code>literal &lt;=&gt; { l=w | (l, w) in literals } &gt;= bound</code> to
     * the solver.
     * <ul>
     *  <li>If <code>type_ &lt; 0</code>, then <code>&lt;=&gt;</code> is a left implication.</li>
     *  <li>If <code>type_ &gt; 0</code>, then <code>&lt;=&gt;</code> is a right implication.</li>
     *  <li>Otherwise, <code>&lt;=&gt;</code> is an equivalence.</li>
     * </ul>
     * If this function returns false, initialization should be stopped and no further
     * functions of the <code>PropagateInit</code> and related objects should be called.
     *
     * @param literal      The literal associated with the constraint.
     * @param literals     The weighted literals of the constraint.
     * @param bound        The bound of the constraint.
     * @param type         Add a weight constraint of the given type_.
     * @param compareEqual A Boolean indicating whether to compare equal or less than equal.
     * @return Returns false if the program became unsatisfiable.
     */
    public boolean addWeightConstraint(int literal, WeightedLiteral[] literals, int bound, WeightConstraintType type, boolean compareEqual) {
        ByteByReference byteByReference = new ByteByReference();
        WeightedLiteral[] weightedLiterals = (WeightedLiteral[]) (new WeightedLiteral()).toArray(literals.length);
        for (int i = 0; i < weightedLiterals.length; i++) {
            weightedLiterals[i].literal = literals[i].literal;
            weightedLiterals[i].weight = literals[i].weight;
        }
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_add_weight_constraint(
                propagateInit,
                literal,
                weightedLiterals,
                new NativeSize(literals.length),
                bound,
                type.getValue(),
                compareEqual ? (byte) 1 : 0,
                byteByReference
        ));
        return byteByReference.getValue() > 0;
    }

    /**
     * Propagates consequences of the underlying problem excluding registered propagators.
     * <p>
     * This function has no effect if SAT-preprocessing is enabled.
     * <p>
     * If this function returns false, initialization should be stopped and no
     * further functions of the <code>PropagateInit</code> and related objects should be
     * called.
     *
     * @return Returns false if the program becomes unsatisfiable.
     */
    public boolean propagate() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_propagate(propagateInit, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Maps the given program literal or condition id to its solver literal.
     *
     * @param literal A program literal or condition id.
     * @return A solver literal.
     */
    public int solverLiteral(int literal) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_solver_literal(propagateInit, literal, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return {@link Assignment} object capturing the top level assignment.
     */
    public Assignment getAssignment() {
        Pointer assignment = Clingo.INSTANCE.clingo_propagate_init_assignment(propagateInit);
        return new Assignment(assignment);
    }

    /**
     * @return {@link PropagatorCheckMode} controlling when to call {@link org.potassco.clingo.internal.Clingo.Propagator#check(byte)}.
     */
    public PropagatorCheckMode getCheckMode() {
        int checkMode = Clingo.INSTANCE.clingo_propagate_init_get_check_mode(propagateInit);
        return PropagatorCheckMode.fromValue(checkMode);
    }

    /**
     * @param mode {@link PropagatorCheckMode} controlling when to call {@link org.potassco.clingo.internal.Clingo.Propagator#check(byte)}.
     */
    public void setCheckMode(PropagatorCheckMode mode) {
        Clingo.INSTANCE.clingo_propagate_init_set_check_mode(propagateInit, mode.getValue());
    }

    /**
     * @return The number of solver threads used in the corresponding solve call.
     */
    public int getAmountThreads() {
        return Clingo.INSTANCE.clingo_propagate_init_number_of_threads(propagateInit);
    }

    /**
     * @return The symbolic atoms captured by a {@link SymbolicAtoms} object.
     */
    public SymbolicAtoms getSymbolicAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_symbolic_atoms(propagateInit, pointerByReference));
        return new SymbolicAtoms(pointerByReference.getValue());
    }

    /**
     * @return The theory atoms captured by a {@link TheoryAtoms} object.
     */
    public TheoryAtoms getTheoryAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_propagate_init_theory_atoms(propagateInit, pointerByReference));
        return new TheoryAtoms(pointerByReference.getValue());
    }


}
