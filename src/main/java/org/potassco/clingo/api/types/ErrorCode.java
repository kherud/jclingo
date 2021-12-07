package org.potassco.clingo.api.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of error codes.
 * <p>
 * Errors can only be recovered from if explicitly mentioned; most
 * functions do not provide strong exception guarantees.  This means that in
 * case of errors associated objects cannot be used further.  If such an
 * object has a free function, this function can and should still be called.
 * @author Josef Schneeberger
 */
public enum ErrorCode {
    /** successful API calls */
	SUCCESS(0),
    /** errors only detectable at runtime like invalid input */
	RUNTIME(1),
    /** wrong usage of the clingo API */
	LOGIC(2),
    /** memory could not be allocated */
	BAD_ALLOC(3),
    /** errors unrelated to clingo */
	UNKNOWN(4);

    private static final Map<Integer, ErrorCode> mapping = new HashMap<>();

	static {
	    for (ErrorCode solveEventType : ErrorCode.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static ErrorCode fromValue(int code) {
		return mapping.get(code);
	}

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }

}
