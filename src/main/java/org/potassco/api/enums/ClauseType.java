package org.potassco.api.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of clause types determining the lifetime of a clause.
 * <p>
 * Clauses in the solver are either cleaned up based on a configurable deletion policy or at the end of a solving step.
 * The values of this enumeration determine if a clause is subject to one of the above deletion strategies.
 * @author Josef Schneeberger
 */
public enum ClauseType {
    /** clause is subject to the solvers deletion policy */
    LEARNT(0, "Learnt"),
    /** clause is not subject to the solvers deletion policy */
    STATIC(1, "Static"),
    /** like ::clingo_clause_type_learnt but the clause is deleted after a solving step */
    VOLATILE(2, "Volatile"),
    /** like ::clingo_clause_type_static but the clause is deleted after a solving step */
    VOLATILE_STATIC(3, "VolatileStatic");

    private static final Map<Integer, ClauseType> mapping = new HashMap<>();

	static {
	    for (ClauseType solveEventType : ClauseType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static ClauseType fromValue(int type) {
		return mapping.get(type);
	}

    private final int type;

    private final String string;

    ClauseType(int type, String string) {
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
