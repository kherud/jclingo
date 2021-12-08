package org.potassco.clingo.backend;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of different external statements.
 * @author Josef Schneeberger
 */
public enum ExternalType {
    /** allow an external to be assigned freely */
    FREE(0, "Free"),
    /** assign an external to true */
    TRUE(1, "True"),
    /** assign an external to false */
    FALSE(2, "False"),
    /** no longer treat an atom as external */
    RELEASE(3, "Release");

    private static final Map<Integer, ExternalType> mapping = new HashMap<>();

	static {
	    for (ExternalType solveEventType : ExternalType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static ExternalType fromValue(int type) {
		return mapping.get(type);
	}

    private final int type;

    private final String string;

    ExternalType(int type, String string) {
        this.type = type;
        this.string = string;
    }

    public int getValue() {
        return type;
    }

    @Override
    public String toString() {
        return string;
    }

}
