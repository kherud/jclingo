package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of different external statements.
 * @ingroup ProgramInspection
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_external_type_e}
 */
//! 
//! 
/* enum clingo_external_type_e {
  clingo_external_type_free    = 0, //!< 
  clingo_external_type_true    = 1, //!< 
  clingo_external_type_false   = 2, //!< 
  clingo_external_type_release = 3, //!< 
}; */
public enum ExternalType {
    /** allow an external to be assigned freely */
    FREE(0, "Free"),
    /** assign an external to true */
    TRUE(1, "True"),
    /** assign an external to false */
    FALSE(2, "False"),
    /** no longer treat an atom as external */
    RELEASE(3, "Release");
    
    private final int type;
    
    private final String string;

    private ExternalType(int type, String string) {
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
