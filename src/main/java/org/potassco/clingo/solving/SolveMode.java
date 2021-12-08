package org.potassco.clingo.solving;


import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of solve modes.<br>
 * enum values<br>
 * <i>native declaration: src/main/clingo/lib/c/clingo.h:2178</i><br>
 * <i>Corresponding type: typedef unsigned clingo_solve_mode_bitset_t;
 * @author Josef Schneeberger
 */
public enum SolveMode {
	/** Enable non-blocking search. */
    ASYNC(1),
    /** Yield models in calls to clingo_solve_handle_model. */
    YIELD(2);

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
