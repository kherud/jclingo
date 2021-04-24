package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of theory term types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_theory_term_type_e}
 */
public enum TheoryTermType {
	/** a tuple term, e.g., `(1,2,3)` */
    TUPLE(0),
	/** a list term, e.g., `[1,2,3]` */
    LIST(1),
	/** a set term, e.g., `{1,2,3}` */
    SET(2),
	/** a function term, e.g., `f(1,2,3)` */
    FUNCTION(3),
	/** a number term, e.g., `42` */
    NUMBER(4),
	/** a symbol term, e.g., `c` */
    SYMBOL(5);

    private int type;

    private TheoryTermType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
