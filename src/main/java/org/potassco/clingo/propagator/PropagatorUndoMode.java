package org.potassco.clingo.propagator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Undo modes for propagators.
 */
public enum PropagatorUndoMode {

	/**
	 * call {@link Propagator#undo(PropagateControl, int[])} for non-empty change lists
	 */
	DEFAULT(0),
	/**
	 * also call {@link Propagator#check(PropagateControl)} when check has been called
	 */
	ALWAYS(1);

	private static final Map<Integer, PropagatorUndoMode> mapping = new HashMap<>();

	static {
		for (PropagatorUndoMode solveEventType : PropagatorUndoMode.values()) {
			mapping.put(
					solveEventType.getValue(),
					solveEventType
			);
		}
	}

	public static PropagatorUndoMode fromValue(int type) {
		return Objects.requireNonNull(mapping.get(type));
	}

	private final int mode;

	PropagatorUndoMode(int mode) {
		this.mode = mode;
	}

	public int getValue() {
		return mode;
	}

}
