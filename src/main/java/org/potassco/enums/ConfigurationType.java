package org.potassco.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of theory term types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_configuration_type_e}
 */
@Deprecated
public enum ConfigurationType {
    /** the entry is a (string) value */
    VALUE(1),
    /** the entry is an array */
    ARRAY(2),
    /** the entry is a map */
    MAP(4),
    /** the entry is a map */
    VALUE_MAP(5),
    /** the entry is a map as well as an array */
	ARRAY_MAP(6);

    private static Map<Integer, ConfigurationType> mapping = new HashMap<>();
    
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

    private ConfigurationType(int type) {
        this.type = type;
    }
    
    public int getValue() {
        return type;
    }
}
