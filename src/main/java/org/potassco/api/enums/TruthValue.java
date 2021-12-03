package org.potassco.api.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * Represents three-valued truth values.
 * @author Josef Schneeberger
 */
public enum TruthValue {
	/** no truth value */
    FREE(0,"Free"),
	/** true */
    TRUE(1,"True"),
	/** false */
    FALSE(2,"False");

    private static final Map<Integer, TruthValue> mapping = new HashMap<>();

	static {
	    for (TruthValue solveEventType : TruthValue.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static TruthValue fromValue(int type) {
		return mapping.get(type);
	}

    private final int type;

    private final String string;

    TruthValue(int type, String string) {
        this.type = type;
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public int getValue() {
        return type;
    }

}
