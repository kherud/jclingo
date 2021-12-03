package org.potassco.api.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of theory term types.
 * @author Josef Schneeberger
 */
public enum ConfigurationType {
    /** the entry is a (string) value */
    VALUE(1),
    /** the entry is an array */
    ARRAY(2),
    /** the entry is a map */
    MAP(4);

    private static final Map<Integer, ConfigurationType> mapping = new HashMap<>();

	static {
	    for (ConfigurationType solveEventType : ConfigurationType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static ConfigurationType fromValue(int type) {
		ConfigurationType ct = mapping.get(type);
		if (ct == null) {
			System.out.println("Unknown ConfigurationType: " + 5);
		}
		return ct;
	}

    private final int type;

    ConfigurationType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }
}
