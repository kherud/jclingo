package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of different heuristic modifiers.
 * @ingroup ProgramInspection
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_heuristic_type_e}
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

    private final int type;

    private final String string;

    private HeuristicType(int type, String string) {
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
