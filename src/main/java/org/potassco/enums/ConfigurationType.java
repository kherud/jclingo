package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of theory term types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_configuration_type_e}
 */
public enum ConfigurationType {
    /** the entry is a (string) value */
    VALUE(1),
    /** the entry is an array */
    ARRAY(2),
    /** the entry is a map */
    MAP(4);
   
    private final int type;

    private ConfigurationType(int type) {
        this.type = type;
    }
    
    public int getValue() {
        return type;
    }
}
