package org.potassco.clingo.solving;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of solve modes.
 */
public enum SolveMode {
	NONE(0),
    ASYNC(1), // non-blocking search
    YIELD(2), // yield models
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
