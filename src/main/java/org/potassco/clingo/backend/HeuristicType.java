package org.potassco.clingo.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration of different heuristic modifiers.
 * @author Josef Schneeberger
 */
public enum HeuristicType {
	/** set the level of an atom */
    LEVEL(0, "Level"),
	/** configure which sign to chose for an atom */
    SIGN(1, "Sign"),
	/** modify VSIDS factor of an atom */
    FACTOR(2, "Factor"),
	/** modify the initial VSIDS score of an atom */
    INIT(3, "Init"),
	/** set the level of an atom and choose a positive sign */
    TRUE(4, "True"),
	/** set the level of an atom and choose a negative sign */
    FALSE(5, "False");

    private static final Map<Integer, HeuristicType> mapping = new HashMap<>();

	static {
	    for (HeuristicType solveEventType : HeuristicType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static HeuristicType fromValue(int type) {
		return Objects.requireNonNull(mapping.get(type));
	}

    private final int type;

    private final String string;

    HeuristicType(int type, String string) {
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
