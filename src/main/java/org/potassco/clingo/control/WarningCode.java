package org.potassco.clingo.control;


import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of warning codes.
 * @author Josef Schneeberger
 */
public enum WarningCode {
    /** undefined arithmetic operation or weight of aggregate */
	OPERATION_UNDEFINED(0),
    /** to report multiple errors; a corresponding runtime error is raised later */
	RUNTIME_ERROR(1),
    /** undefined atom in program */
	ATOM_UNDEFINED(2),
    /** same file included multiple times */
	FILE_INCLUDED(3),
    /** CSP variable with unbounded domain */
	VARIABLE_UNBOUNDED(4),
    /** global variable in tuple of aggregate element */
	GLOBAL_VARIABLE(5),
    /** other kinds of warnings */
	OTHER(6);

    private static final Map<Integer, WarningCode> mapping = new HashMap<>();

	static {
	    for (WarningCode solveEventType : WarningCode.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static WarningCode fromValue(int code) {
		return mapping.get(code);
	}

    private final int code;

    WarningCode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }

}
