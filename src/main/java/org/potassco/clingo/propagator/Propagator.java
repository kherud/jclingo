package org.potassco.clingo.propagator;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.potassco.clingo.internal.NativeSize;

import java.util.Arrays;
import java.util.List;

/**
 * An instance of this struct has to be registered with a solver to implement a custom propagator.
 *
 * Not all callbacks have to be implemented and can be set to NULL if not needed.
 * @author Josef Schneeberger
 */
public abstract class Propagator extends Structure {

	/**
	 * This function is called once before each solving step.
	 * It is used to map relevant program literals to solver literals, add watches for solver literals, and initialize the data structures used during propagation.
	 *
	 * This is the last point to access symbolic and theory atoms.
	 * Once the search has started, they are no longer accessible.
	 *
	 * @param init initizialization object
	 * @param data user data for the callback
	 * @return whether the call was successful
	 */
	public boolean init(Pointer init, Pointer data) {
		return true;
	}

	/**
	 * Can be used to propagate solver literals given a @link clingo_assignment_t partial assignment@endlink.
	 *
	 * Called during propagation with a non-empty array of @link clingo_propagate_init_add_watch() watched solver literals@endlink
	 * that have been assigned to true since the last call to either propagate, undo, (or the start of the search) - the change set.
	 * Only watched solver literals are contained in the change set.
	 * Each literal in the change set is true w.r.t. the current @link clingo_assignment_t assignment@endlink.
	 * clingo_propagate_control_add_clause() can be used to add clauses.
	 * If a clause is unit resulting, it can be propagated using @ref clingo_propagate_control_propagate().
	 * If the result of either of the two methods is false, the propagate function must return immediately.
	 *
	 * This function can be called from different solving threads.
	 * Each thread has its own assignment and id, which can be obtained using @ref clingo_propagate_control_thread_id().
	 *
	 * @param control control object for the target solver
	 * @param changes the change set
	 * @param size the size of the change set
	 * @param data user data for the callback
	 * @return whether the call was successful
	 */
	public boolean propagate(Pointer control, Pointer changes, NativeSize size, Pointer data) {
		return true;
	}

	/**
	 * Called whenever a solver undoes assignments to watched solver literals.
	 *
	 * This callback is meant to update assignment dependent state in the propagator.
	 *
	 * No clauses must be propagated in this callback and no errors should be set.
	 *
	 * @param control control object for the target solver
	 * @param changes the change set
	 * @param size the size of the change set
	 * @param data user data for the callback
	 */
	public boolean undo(Pointer control, Pointer changes, NativeSize size, Pointer data) {
		return true;
	}

	/**
	 * This function is similar to @ref clingo_propagate_control_propagate() but is called without a change set on propagation fixpoints.
	 *
	 * When exactly this function is called, can be configured using the @ref clingo_propagate_init_set_check_mode() function.
	 *
	 * This function is called even if no watches have been added.
	 *
	 * @param control control object for the target solver
	 * @param data user data for the callback
	 * @return whether the call was successful
	 */
	public boolean check(Pointer control, Pointer data) {
		return true;
	}

	/**
	 * This function allows a propagator to implement domain-specific heuristics.
	 *
	 * It is called whenever propagation reaches a fixed point and
	 * should return a free solver literal that is to be assigned true.
	 * In case multiple propagators are registered,
	 * this function can return 0 to let a propagator registered later make a decision.
	 * If all propagators return 0, then the fallback literal is
	 *
	 * @param threadId the solver's thread id
	 * @param assignment the assignment of the solver
	 * @param fallback the literal chosen by the solver's heuristic
	 * @param decision the literal to make true
	 * @return whether the call was successful
	 */
	public boolean decide(int threadId, Pointer assignment, int fallback, Pointer data, int decision) {
		return true;
	}

	/**
	 * JNA DECLARATIONS
	 **/


	protected List<String> getFieldOrder() {
		return Arrays.asList("init", "propagate", "undo", "check", "decide");
	}

	public PropagatorInitCallback init = this::init;
	public PropagatorPropagateCallback propagate = this::propagate;
	public PropagatorUndoCallback undo = this::undo;
	public PropagatorCheckCallback check = this::check;
	public PropagatorDecideCallback decide = this::decide;

	private interface PropagatorInitCallback extends Callback {
		boolean callback(Pointer init, Pointer data);
	}

	private interface PropagatorPropagateCallback extends Callback {
		boolean callback(Pointer control, Pointer changes, NativeSize size, Pointer data);
	}

	private interface PropagatorUndoCallback extends Callback {
		void callback(Pointer control, Pointer changes, NativeSize size, Pointer data);
	}

	private interface PropagatorCheckCallback extends Callback {
		boolean check(Pointer control, Pointer data);
	}

	private interface PropagatorDecideCallback extends Callback {
		boolean callback(int threadId, Pointer assignment, int fallback, Pointer data, int decision);
	}
}
