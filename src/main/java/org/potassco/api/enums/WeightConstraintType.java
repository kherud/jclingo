package org.potassco.api.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of weight constraint types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_weight_constraint_type_e}
 */
public enum WeightConstraintType {
	/** the weight constraint implies the literal */
	IMPLICATION_LEFT(-1),
	/** the literal implies the weight constraint */
	IMPLICATION_RIGHT(1),
	/** the weight constraint is equivalent to the literal */
	EQUIVALENCE(0);

    private static Map<Integer, WeightConstraintType> mapping = new HashMap<>();

	static {
	    for (WeightConstraintType solveEventType : WeightConstraintType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static WeightConstraintType fromValue(int type) {
		return mapping.get(type);
	}

    private int type;

    private WeightConstraintType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
