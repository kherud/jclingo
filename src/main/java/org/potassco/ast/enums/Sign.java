package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of signs.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_sign_e}
 */
public enum Sign {
 
    NO_SIGN(0,""),
    NEGATION(1,"not"),
    DOUBLE_NEGATION(2,"not not");
    
    private final int sign;

    private final String string;
    
    private Sign(int sign, String string) {
        this.sign = sign;
        this.string = string;
    }

    public int getValue() {
        return sign;
    }
    
    @Override
    public String toString() {
        return string;
    }
    
}
