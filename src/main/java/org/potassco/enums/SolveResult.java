package org.potassco.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of solve result types.
 * @note documented in Control Module
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_solve_result_e}
 */
public enum SolveResult {
	SATISFIABLE(1),
	UNSATISFIABLE(2),
	EXHAUSTED(4),
	INTERRUPTED(8);

    private static Map<Integer, SolveResult> mapping = new HashMap<>();
    
	static {
	    for (SolveResult solveEventType : SolveResult.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	
	public static SolveResult fromValue(int code) {
		return mapping.get(code);
	}

    private int code;

    private SolveResult(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
    
}
