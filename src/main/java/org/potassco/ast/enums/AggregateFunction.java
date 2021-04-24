package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of aggregate functions.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_aggregate_function_e}
 */
public enum AggregateFunction {
    
    COUNT(0,"^"),
    SUM(1,"?"),
    SUM_PLUS(2,"&"),
    MIN(3,"+"),
    MAX(4,"-");
            
    private final int function;

    private final String string;

    private AggregateFunction(int function, String string) {
        this.function = function;
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public int getValue() {
        return function;
    }
    
}
