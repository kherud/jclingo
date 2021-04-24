package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of comparison relations.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_comparison_operator_e}
 */
public enum ComparisonOperator {
 
    GREATER_THAN(0,">"),
    LESS_THAN(1,"<"),
    LESS_EQUAL(2,"<="),
    GREATER_EQUAL(3,">="),
    NOT_EQUAL(4, "!="),
    EQUAL(5, "==");
    
    private final int operator;

    private final String string;
    
    private ComparisonOperator(int operator, String string) {
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
