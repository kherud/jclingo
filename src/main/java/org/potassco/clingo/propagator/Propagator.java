package org.potassco.clingo.propagator;

/**
 * An instance of this struct has to be registered with a solver to implement a custom propagator.
 * <p>
 * Not all callbacks have to be implemented and can be set to NULL or be left with their default implementation.
 */
public interface Propagator {

    /**
     * This function is called once before each solving step.
     * It is used to map relevant program literals to solver literals, add watches for solver literals, and initialize the data structures used during propagation.
     * <p>
     * This is the last point to access symbolic and theory atoms.
     * Once the search has started, they are no longer accessible.
     *
     * @param init initizialization object
     */
    default void init(PropagateInit init) {

    }

    /**
     * Can be used to propagate solver literals given a {@link Assignment partial assignment}.
     * <p>
     * Called during propagation with a non-empty array of {@link PropagateInit#addWatch} watched solver literals}
     * that have been assigned to true since the last call to either propagate, undo, (or the start of the search) - the change set.
     * Only watched solver literals are contained in the change set.
     * Each literal in the change set is true w.r.t. the current {@link Assignment}.
     * {@link PropagateControl#addClause} can be used to add clauses.
     * If a clause is unit resulting, it can be propagated using {@link PropagateControl#propagate}.
     * If the result of either of the two methods is false, the propagate function must return immediately.
     * <p>
     * This function can be called from different solving threads.
     * Each thread has its own assignment and id, which can be obtained using {@link PropagateControl#getThreadId}.
     *
     * @param control control object for the target solver
     * @param changes the change set
     */
    default void propagate(PropagateControl control, int[] changes) {

    }

    /**
     * Called whenever a solver undoes assignments to watched solver literals.
     * <p>
     * This callback is meant to update assignment dependent state in the propagator.
     * <p>
     * No clauses must be propagated in this callback and no errors should be set.
     *
     * @param control control object for the target solver
     * @param changes the change set
     */
    default void undo(PropagateControl control, int[] changes) {

    }

    /**
     * This function is similar to {@link Propagator#propagate} but is called without a change set on propagation fixpoints.
     * <p>
     * When exactly this function is called, can be configured using the {@link PropagateInit#setCheckMode} function.
     * <p>
     * This function is called even if no watches have been added.
     *
     * @param control control object for the target solver
     */
    default void check(PropagateControl control) {

    }

    /**
     * This function allows a propagator to implement domain-specific heuristics.
     * <p>
     * It is called whenever propagation reaches a fixed point and
     * should return a free solver literal that is to be assigned true.
     * In case multiple propagators are registered,
     * this function can return 0 to let a propagator registered later make a decision.
     * If all propagators return 0, then the fallback literal is used.
     *
     * @param threadId the solver's thread id
     * @param assignment the assignment of the solver
     * @param fallback the literal chosen by the solver's heuristic
     * @return the literal to make true
     */
    default int decide(int threadId, Assignment assignment, int fallback) {
        return fallback;
    }
}
