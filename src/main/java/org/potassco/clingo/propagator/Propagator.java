package org.potassco.clingo.propagator;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import org.potassco.clingo.internal.NativeSize;

import java.util.Arrays;
import java.util.List;

/**
 * An instance of this struct has to be registered with a solver to implement a custom propagator.
 *
 * Not all callbacks have to be implemented and can be set to NULL or be left with their default implementation.
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
	 */
	public void init(PropagateInit init) {

	}

	/**
	 * Can be used to propagate solver literals given a {@link Assignment partial assignment}.
	 *
	 * Called during propagation with a non-empty array of {@link PropagateInit#addWatch} watched solver literals}
	 * that have been assigned to true since the last call to either propagate, undo, (or the start of the search) - the change set.
	 * Only watched solver literals are contained in the change set.
	 * Each literal in the change set is true w.r.t. the current {@link Assignment}.
	 * {@link PropagateControl#addClause} can be used to add clauses.
	 * If a clause is unit resulting, it can be propagated using {@link PropagateControl#propagate}.
	 * If the result of either of the two methods is false, the propagate function must return immediately.
	 *
	 * This function can be called from different solving threads.
	 * Each thread has its own assignment and id, which can be obtained using {@link PropagateControl#getThreadId}.
	 *
	 * @param control control object for the target solver
	 * @param changes the change set
	 */
	public void propagate(PropagateControl control, int[] changes) {

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
	 */
	public void undo(PropagateControl control, int[] changes) {

	}

	/**
	 * This function is similar to {@link Propagator#propagate} but is called without a change set on propagation fixpoints.
	 *
	 * When exactly this function is called, can be configured using the {@link PropagateInit#setCheckMode} function.
	 *
	 * This function is called even if no watches have been added.
	 *
	 * @param control control object for the target solver
	 */
	public void check(PropagateControl control) {

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
	 */
	public void decide(int threadId, Assignment assignment, int fallback, IntByReference decision) {

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
		default boolean callback(Pointer init, Pointer data) {
			call(new PropagateInit(init));
			return true;
		}

		void call(PropagateInit init);
	}

	private interface PropagatorPropagateCallback extends Callback {
		default boolean callback(Pointer control, Pointer changes, NativeSize size, Pointer data) {
			int intSize = size.intValue();
			int[] literals = intSize > 0 ? changes.getIntArray(0, intSize) : new int[0];
			call(new PropagateControl(control), literals);
			return true;
		}

		void call(PropagateControl control, int[] literals);
	}

	private interface PropagatorUndoCallback extends Callback {
		default void callback(Pointer control, Pointer changes, NativeSize size, Pointer data) {
			int intSize = size.intValue();
			int[] literals = intSize > 0 ? changes.getIntArray(0, intSize) : new int[0];
			call(new PropagateControl(control), literals);
		}

		void call(PropagateControl control, int[] changes);
	}

	private interface PropagatorCheckCallback extends Callback {
		default boolean check(Pointer control, Pointer data) {
			call(new PropagateControl(control));
			return true;
		}

		void call(PropagateControl control);
	}

	private interface PropagatorDecideCallback extends Callback {
		default boolean callback(int threadId, Pointer assignment, int fallback, Pointer data, IntByReference decision) {
			call(threadId, new Assignment(assignment), fallback, decision);
			return true;
		}

		void call(int threadId, Assignment assignment, int fallbackLiteral, IntByReference decision);
	}
}
