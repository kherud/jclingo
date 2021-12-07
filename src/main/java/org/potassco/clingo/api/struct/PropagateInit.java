package org.potassco.clingo.api.struct;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.api.types.NativeSize;
import org.potassco.clingo.api.types.PropagatorCheckMode;
import org.potassco.clingo.api.types.WeightConstraintType;
import org.potassco.clingo.control.SymbolicAtoms;
import org.potassco.clingo.theory.TheoryAtoms;

/**
 * Object that is used to initialize a propagator before each solving step.
 */
public class PropagateInit implements ErrorChecking {

    private final Pointer propagateInit;

    public PropagateInit(Pointer propagateInit) {
        this.propagateInit = propagateInit;
    }

    /**
     * Statically adds the given clause to the problem.
     * If this function returns false, initialization should be stopped and no
     * further functions of the `PropagateInit` and related objects should be called.
     *
     * @param clause The clause over solver literals to add.
     * @return Returns false if the program becomes unsatisfiable.
     */
    public boolean addClause(int[] clause) {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_propagate_init_add_clause(propagateInit, clause, new NativeSize(clause.length), byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Statically adds a literal to the solver.
     *
     * To be able to use the variable in clauses during propagation or add
     * watches to it, it has to be frozen. Otherwise, it might be removed
     * during preprocessing.
     *
     * If literals are added to the solver, subsequent calls to `add_clause` and
     * `propagate` are expensive. It is best to add literals in batches.
     *
     * @param freeze Whether to freeze the variable.
     * @return Returns the added literal.
     */
    public int addLiteral(boolean freeze) {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_propagate_init_add_literal(propagateInit, freeze, intByReference));
        return intByReference.getValue();
    }

    /**
     * Extends the solver's minimize constraint with the given weighted literal.
     * @param literal The literal to add.
     * @param weight The weight of the literal.
     * @param priority The priority of the literal.
     */
    public void addMinimize(int literal, int weight, int priority) {
        checkError(Clingo.INSTANCE.clingo_propagate_init_add_minimize(propagateInit, literal, weight, priority));
    }

    /**
     * Add a watch for the solver literal in the given phase.
     * All active threads will watch the literal.
     *
     * @param literal The solver literal to watch.
     */
    public void addWatch(int literal) {
        checkError(Clingo.INSTANCE.clingo_propagate_init_add_watch(propagateInit, literal));
    }

    /**
     * Add a watch to a specific thread for the solver literal in the given phase.
     *
     * @param literal The solver literal to watch.
     * @param threadId The id of the thread to watch the literal.
     */
    public void addWatch(int literal, int threadId) {
        checkError(Clingo.INSTANCE.clingo_propagate_init_add_watch_to_thread(propagateInit, literal, threadId));
    }

    /**
     * Remove the watch for the solver literal in the given phase.
     * The watch is removed from all active threads.
     *
     * @param literal The solver literal to remove the watch from.
     */
    public void removeWatch(int literal) {
        checkError(Clingo.INSTANCE.clingo_propagate_init_remove_watch(propagateInit, literal));
    }

    /**
     * Remove the watch for the solver literal in the given phase.
     *
     * @param literal The solver literal to remove the watch from.
     * @param threadId The id of the thread from which to remove the watch.
     */
    public void removeWatch(int literal, int threadId) {
        checkError(Clingo.INSTANCE.clingo_propagate_init_remove_watch_from_thread(propagateInit, literal, threadId));
    }

    /**
     * Freeze the given solver literal.
     *
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
        checkError(Clingo.INSTANCE.clingo_propagate_init_freeze_literal(propagateInit, literal));
    }

    /**
     * Statically adds a constraint of form
     *
     *      literal <=> { l=w | (l, w) in literals } >= bound
     *
     * to the solver.
     *
     * - If `type_ < 0`, then `<=>` is a left implication.
     * - If `type_ > 0`, then `<=>` is a right implication.
     * - Otherwise, `<=>` is an equivalence.
     *
     * If this function returns false, initialization should be stopped and no further
     * functions of the `PropagateInit` and related objects should be called.
     *
     * @param literal The literal associated with the constraint.
     * @param literals The weighted literals of the constraint.
     * @param bound The bound of the constraint.
     * @param type Add a weight constraint of the given type_.
     * @param compareEqual A Boolean indicating whether to compare equal or less than equal.
     * @return Returns false if the program became unsatisfiable.
     */
    public boolean addWeightConstraint(int literal, WeightedLiteral[] literals, int bound, WeightConstraintType type, boolean compareEqual) {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_propagate_init_add_weight_constraint(
                propagateInit,
                literal,
                literals,
                new NativeSize(literals.length),
                bound,
                type.getValue(),
                compareEqual,
                byteByReference
        ));
        return byteByReference.getValue() > 0;
    }

    /**
     * Propagates consequences of the underlying problem excluding registered propagators.
     *
     * This function has no effect if SAT-preprocessing is enabled.
     *
     * If this function returns false, initialization should be stopped and no
     * further functions of the `PropagateInit` and related objects should be
     * called.
     *
     * @return Returns false if the program becomes unsatisfiable.
     */
    public boolean propagate() {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_propagate_init_propagate(propagateInit, byteByReference));
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
        checkError(Clingo.INSTANCE.clingo_propagate_init_solver_literal(propagateInit, literal, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return `Assignment` object capturing the top level assignment.
     */
    public Assignment getAssignment() {
        // TODO: fix Clingo Interface method signature
        Pointer assignment = Clingo.INSTANCE.clingo_propagate_init_assignment(propagateInit);
        return new Assignment(assignment);
    }

    /**
     * @return `PropagatorCheckMode` controlling when to call `Propagator.check`.
     */
    public PropagatorCheckMode getPropagatorCheckMode() {
        int checkMode = Clingo.INSTANCE.clingo_propagate_init_get_check_mode(propagateInit);
        return PropagatorCheckMode.fromValue(checkMode);
    }

    /**
     * @param mode `PropagatorCheckMode` controlling when to call `Propagator.check`.
     */
    public void setPropagatorCheckMode(PropagatorCheckMode mode) {
        Clingo.INSTANCE.clingo_propagate_init_set_check_mode(propagateInit, mode.getValue());
    }

    /**
     * @return The number of solver threads used in the corresponding solve call.
     */
    public int getAmountThreads() {
        return Clingo.INSTANCE.clingo_propagate_init_number_of_threads(propagateInit);
    }

    /**
     * @return The symbolic atoms captured by a `SymbolicAtoms` object.
     */
    public SymbolicAtoms getSymbolicAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_propagate_init_symbolic_atoms(propagateInit, pointerByReference));
        return new SymbolicAtoms(pointerByReference.getValue());
    }

    /**
     * @return The theory atoms captured by a `TheoryAtoms` object.
     */
    public TheoryAtoms getTheoryAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_propagate_init_theory_atoms(propagateInit, pointerByReference));
        return new TheoryAtoms(pointerByReference.getValue());
    }


}
