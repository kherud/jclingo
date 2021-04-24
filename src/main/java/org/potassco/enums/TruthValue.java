package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Represents three-valued truth values.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_truth_value_e}
 */
public enum TruthValue {
	/** no truth value */
    FREE(0,"Free"),
	/** true */
    TRUE(1,"True"),
	/** false */
    FALSE(2,"False");

    private int type;

    private String string;
    
    private TruthValue(int type, String string) {
        this.type = type;
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public int getValue() {
        return type;
    }
   
}
