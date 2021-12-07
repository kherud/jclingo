package org.potassco.clingo.api.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported check modes for propagators.
 * <p>Note that total checks are subject to the lock when a model is found.
 * This means that information from previously found models can be used to discard assignments in check calls.
 * @author Josef Schneeberger
 */
public enum PropagatorCheckMode {
	/** do not call @ref ::clingo_propagator::check() at all */
    NONE(0),
	/** call @ref ::clingo_propagator::check() on total assignments */
    TOTAL(1),
	/** call @ref ::clingo_propagator::check() on propagation fixpoints */
    PARTIAL(2),
	/** call @ref ::clingo_propagator::check() on propagation fixpoints and total assignments */
    BOTH(3);

    private static final Map<Integer, PropagatorCheckMode> mapping = new HashMap<>();

	static {
	    for (PropagatorCheckMode solveEventType : PropagatorCheckMode.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static PropagatorCheckMode fromValue(int type) {
		return mapping.get(type);
	}

    private final int mode;

    PropagatorCheckMode(int mode) {
        this.mode = mode;
    }

    public int getValue() {
        return mode;
    }

}
