package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of theory operators.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_theory_operator_type_e}
 */
public enum TheoryOperatorType {
    /** An unary theory operator. */
    UNARY(0,"unary"),
    /** A left associative binary operator. */
    BINARY_LEFT(1,"binary, left"),
    /** A right associative binary operator. */
    BINARY_RIGHT(2,"binary, right");
             
    private final int type;
    private final String string;

    private TheoryOperatorType(int type, String string) {
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
