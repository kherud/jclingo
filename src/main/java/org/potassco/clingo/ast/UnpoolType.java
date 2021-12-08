package org.potassco.clingo.ast;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration to configure unpooling.
 * @author Josef Schneeberger
 */
public enum UnpoolType {
	/** To only unpool conditions of conditional literals. */
	CONDITION(1),
	/** To unpool everything except conditions of conditional literals. */
	OTHER(2),
	/** To unpool everything. */
	ALL(3);

    private static final Map<Integer, UnpoolType> mapping = new HashMap<>();

	static {
	    for (UnpoolType solveEventType : UnpoolType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static UnpoolType fromValue(int type) {
		return mapping.get(type);
	}

    private final int type;

    UnpoolType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
