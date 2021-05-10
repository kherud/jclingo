package org.potassco.jna;

import java.util.Arrays;
import java.util.List;

import org.potassco.cpp.clingo_h;
import com.sun.jna.Callback;
import org.potassco.jna.PropagatorSt.PropagatorCheckCallback;
import org.potassco.jna.PropagatorSt.PropagatorDecideCallback;
import org.potassco.jna.PropagatorSt.PropagatorInitCallback;
import org.potassco.jna.PropagatorSt.PropagatorPropagateCallback;
import org.potassco.jna.PropagatorSt.PropagatorUndoCallback;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * An instance of this struct has to be registered with a solver to implement a custom propagator.
 *
 * Not all callbacks have to be implemented and can be set to NULL if not needed.
 * @see PropagatorSt
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_propagator}
 */
public class PropagatorSt extends Structure {

	/**
	 * This function is called once before each solving step.
	 * It is used to map relevant program literals to solver literals, add watches for solver literals, and initialize the data structures used during propagation.
	 *
	 * @note This is the last point to access symbolic and theory atoms.
	 * Once the search has started, they are no longer accessible.
	 *
	 * @param[in] init initizialization object
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 * @see ::clingo_propagator_init_callback_t
	 */
	//  bool (*init) (clingo_propagate_init_t *init, void *data);
//	public boolean init(PropagateInitSt init, Object data) {}
	/**
	 * Can be used to propagate solver literals given a @link clingo_assignment_t partial assignment@endlink.
	 *
	 * Called during propagation with a non-empty array of @link clingo_propagate_init_add_watch() watched solver literals@endlink
	 * that have been assigned to true since the last call to either propagate, undo, (or the start of the search) - the change set.
	 * Only watched solver literals are contained in the change set.
	 * Each literal in the change set is true w.r.t. the current @link clingo_assignment_t assignment@endlink.
	 * @ref clingo_propagate_control_add_clause() can be used to add clauses.
	 * If a clause is unit resulting, it can be propagated using @ref clingo_propagate_control_propagate().
	 * If the result of either of the two methods is false, the propagate function must return immediately.
	 *
	 * The following snippet shows how to use the methods to add clauses and propagate consequences within the callback.
	 * The important point is to return true (true to indicate there was no error) if the result of either of the methods is false.
	 * ~~~~~~~~~~~~~~~{.c}
	 * bool result;
	 * clingo_literal_t clause[] = { ... };
	 *
	 * // add a clause
	 * if (!clingo_propagate_control_add_clause(control, clause, clingo_clause_type_learnt, &result) { return false; }
	 * if (!result) { return true; }
	 * // propagate its consequences
	 * if (!clingo_propagate_control_propagate(control, &result) { return false; }
	 * if (!result) { return true; }
	 *
	 * // add further clauses and propagate them
	 * ...
	 *
	 * return true;
	 * ~~~~~~~~~~~~~~~
	 *
	 * @note
	 * This function can be called from different solving threads.
	 * Each thread has its own assignment and id, which can be obtained using @ref clingo_propagate_control_thread_id().
	 *
	 * @param[in] control control object for the target solver
	 * @param[in] changes the change set
	 * @param[in] size the size of the change set
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 * @see ::clingo_propagator_propagate_callback_t
	 */
	//  bool (*propagate) (clingo_propagate_control_t *control, clingo_literal_t const *changes, size_t size, void *data);
	/**
	 * Called whenever a solver undoes assignments to watched solver literals.
	 *
	 * This callback is meant to update assignment dependent state in the propagator.
	 *
	 * @note No clauses must be propagated in this callback and no errors should be set.
	 *
	 * @param[in] control control object for the target solver
	 * @param[in] changes the change set
	 * @param[in] size the size of the change set
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 * @see ::clingo_propagator_undo_callback_t
	 */
	//  void (*undo) (clingo_propagate_control_t const *control, clingo_literal_t const *changes, size_t size, void *data);
	/**
	 * This function is similar to @ref clingo_propagate_control_propagate() but is called without a change set on propagation fixpoints.
	 *
	 * When exactly this function is called, can be configured using the @ref clingo_propagate_init_set_check_mode() function.
	 *
	 * @note This function is called even if no watches have been added.
	 *
	 * @param[in] control control object for the target solver
	 * @param[in] data user data for the callback
	 * @return whether the call was successful
	 * @see ::clingo_propagator_check_callback_t
	 */
	//  bool (*check) (clingo_propagate_control_t *control, void *data);
	/**
	 * This function allows a propagator to implement domain-specific heuristics.
	 *
	 * It is called whenever propagation reaches a fixed point and
	 * should return a free solver literal that is to be assigned true.
	 * In case multiple propagators are registered,
	 * this function can return 0 to let a propagator registered later make a decision.
	 * If all propagators return 0, then the fallback literal is
	 *
	 * @param[in] thread_id the solver's thread id
	 * @param[in] assignment the assignment of the solver
	 * @param[in] fallback the literal choosen by the solver's heuristic
	 * @param[out] decision the literal to make true
	 * @return whether the call was successful
	 */
	/*    bool (*decide) (clingo_id_t thread_id, clingo_assignment_t const *assignment, clingo_literal_t fallback, void *data, clingo_literal_t *decision);
	} clingo_propagator_t; */

	interface PropagatorDecideCallback extends Callback {
		boolean callback(int init, int data);
	}

	interface PropagatorCheckCallback extends Callback {

	}

	interface PropagatorUndoCallback extends Callback {

	}

	interface PropagatorPropagateCallback extends Callback {

	}

	interface PropagatorInitCallback extends Callback {

	}

	public PropagatorInitCallback init;
	public PropagatorPropagateCallback propagate;
	public PropagatorUndoCallback undo;
	public PropagatorCheckCallback check;
	public PropagatorDecideCallback decide;
	
	public PropagatorSt(PropagatorInitCallback init, PropagatorPropagateCallback propagate, PropagatorUndoCallback undo,
			PropagatorCheckCallback check, PropagatorDecideCallback decide) {
		super();
		this.init = init;
		this.propagate = propagate;
		this.undo = undo;
		this.check = check;
		this.decide = decide;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("init", "propagate", "undo", "check", "decide");
	}

}
