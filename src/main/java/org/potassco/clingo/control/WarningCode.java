package org.potassco.clingo.control;


import org.potassco.clingo.internal.Clingo;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of warning codes.
 */
public enum WarningCode {
	OPERATION_UNDEFINED(0), // undefined arithmetic operation or weight of aggregate
	RUNTIME_ERROR(1), // to report multiple errors; a corresponding runtime error is raised later
	ATOM_UNDEFINED(2), // undefined atom in program
	FILE_INCLUDED(3), // same file included multiple times
	VARIABLE_UNBOUNDED(4), // CSP variable with unbounded domain
	GLOBAL_VARIABLE(5), // global variable in tuple of aggregate element
	OTHER(6); // other kinds of warnings

	private final int code;

	WarningCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return Clingo.INSTANCE.clingo_warning_string(code);
	}

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

    public int getValue() {
        return code;
    }

}
