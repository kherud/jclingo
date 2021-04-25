package org.potassco.ast.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration to configure unpooling.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_unpool_type_e}
 */
public enum UnpoolType {
	/** To only unpool conditions of conditional literals. */
	CONDITION(1),
	/** To unpool everything except conditions of conditional literals. */
	OTHER(2),
	/** To unpool everything. */
	ALL(3);

    private static Map<Integer, UnpoolType> mapping = new HashMap<>();
    
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

    private int type;

    private UnpoolType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
