package org.potassco.clingo.propagator;

public enum UndoMode {

	/**
	 * Call {@link Propagator#undo(PropagateControl, int[])} for non-empty change lists
	 */
	DEFAULT(0),

	/**
	 * Also call {@link Propagator#check(PropagateControl)} when check has been called
	 */
	ALWAYS(1);

	private final int code;

	UndoMode(int code) {
		this.code = code;
	}
}
