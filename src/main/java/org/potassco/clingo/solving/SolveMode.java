package org.potassco.clingo.solving;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of solve modes.
 */
public enum SolveMode {
	/** Enable non-blocking search. */
    ASYNC(1),
    /** Yield models in calls to clingo_solve_handle_model. */
    YIELD(2),

	ASYNC_YIELD(3);

    private static final Map<Integer, SolveMode> mapping = new HashMap<>();

	static {
	    for (SolveMode solveEventType : SolveMode.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static SolveMode fromValue(int type) {
		return mapping.get(type);
	}

    private final int mode;

    SolveMode(int mode) {
        this.mode = mode;
    }

    public int getValue() {
        return mode;
    }

}
