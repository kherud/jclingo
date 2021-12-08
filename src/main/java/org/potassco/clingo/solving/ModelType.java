package org.potassco.clingo.solving;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for the different model types.
 * @author Josef Schneeberger
 */
public enum ModelType {
	/** The model represents a stable model. */
    STABLE_MODEL(0),
	/** The model represents a set of brave consequences. */
    BRAVE_CONSEQUENCES(1),
	/** The model represents a set of cautious consequences. */
    CAUTIOUS_CONSEQUENCES(2);

    private static final Map<Integer, ModelType> mapping = new HashMap<>();

	static {
	    for (ModelType solveEventType : ModelType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static ModelType fromValue(int type) {
		return mapping.get(type);
	}

    private final int type;

    ModelType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
