package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of binary operators.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_binary_operator_e}
 */
public enum BinaryOperator {
    
    XOR(0,"^"),
    OR(1,"?"),
    AND(2,"&"),
    PLUS(3,"+"),
    MINUS(4,"-"),
    MULTIPLICATION(5,"*"),
    DIVISION(6,"/"),
    MODULO(7,"\\"),
    POWER(8,"**");
            
    private int operator;
    
    private String string;

    private BinaryOperator(int operator, String string) {
        this.operator = operator;
        this.string = string;
    }


    @Override
    public String toString() {
        return string;
    }

    public int getValue() {
        return operator;
    }

}
