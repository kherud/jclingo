package org.potassco.ast.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of unary operators.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_unary_operator_e}
 */
public enum UnaryOperator {

    ABSOLUTE(2,"|","|"),
    MINUS(0,"-",""),
    NEGATION(1,"~","");

    private static Map<Integer, UnaryOperator> mapping = new HashMap<>();
    
	static {
	    for (UnaryOperator solveEventType : UnaryOperator.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	
	public static UnaryOperator fromValue(int type) {
		return mapping.get(type);
	}

    private final int operator;

    private final String left;

    private final String right;
    
    private UnaryOperator(int operator, String left, String right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public int getValue() {
        return operator;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

}
