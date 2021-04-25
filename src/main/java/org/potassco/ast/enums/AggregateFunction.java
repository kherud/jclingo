package org.potassco.ast.enums;

import java.util.HashMap;
import java.util.Map;

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

    private static Map<Integer, AggregateFunction> mapping = new HashMap<>();
    
	static {
	    for (AggregateFunction solveEventType : AggregateFunction.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	
	public static AggregateFunction fromValue(int type) {
		return mapping.get(type);
	}

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
