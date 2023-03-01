package org.potassco.clingo.ast;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of signs.
 * @author Josef Schneeberger
 */
public enum AstType {
	// terms
    ID,
    VARIABLE,
    SYMBOLIC_TERM,
    UNARY_OPERATION,
    BINARY_OPERATION,
    INTERVAL,
    FUNCTION,
    POOL,
    // simple atoms
    BOOLEAN_CONSTANT,
    SYMBOLIC_ATOM,
    COMPARISON,
    // aggregates
    AGGREGATE_GUARD,
    CONDITIONAL_LITERAL,
    AGGREGATE,
    BODY_AGGREGATE_ELEMENT,
    BODY_AGGREGATE,
    HEAD_AGGREGATE_ELEMENT,
    HEAD_AGGREGATE,
    DISJUNCTION,
    // theory atoms
    THEORY_SEQUENCE,
    THEORY_FUNCTION,
    THEORY_UNPARSED_TERM_ELEMENT,
    THEORY_UNPARSED_TERM,
    THEORY_GUARD,
    THEORY_ATOM_ELEMENT,
    THEORY_ATOM,
    // literals
    LITERAL,
    // theory definition
    THEORY_OPERATOR_DEFINITION,
    THEORY_TERM_DEFINITION,
    THEORY_GUARD_DEFINITION,
    THEORY_ATOM_DEFINITION,
    // statements
    RULE,
    DEFINITION,
    SHOW_SIGNATURE,
    SHOW_TERM,
    MINIMIZE,
    SCRIPT,
    PROGRAM,
    EXTERNAL,
    EDGE,
    HEURISTIC,
    PROJECT_ATOM,
    PROJECT_SIGNATURE,
    DEFINED,
    THEORY_DEFINITION;

    private static final Map<Integer, AstType> mapping = new HashMap<>();

	static {
	    for (AstType type : AstType.values()) {
	    	mapping.put(
	          type.ordinal(),
	          type
	        );
	    }
	}

	public static AstType fromOrdinal(int type) {
		return mapping.get(type);
	}

}
