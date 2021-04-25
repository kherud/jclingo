package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of signs.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_type_e}
 */
public enum Type {
	// terms
    ID,
    VARIABLE,
    SYMBOLIC_TERM,
    UNARY_OPERATION,
    BINARY_OPERATION,
    INTERVAL,
    FUNCTION,
    POOL,
    // csp terms
    CSP_PRODUCT,
    CSP_SUM,
    CSP_GUARD,
    // simple atoms
    BOOLEAN_CONSTANT,
    SYMBOLIC_ATOM,
    COMPARISON,
    CSP_LITERAL,
    // aggregates
    AGGREGATE_GUARD,
    CONDITIONAL_LITERAL,
    AGGREGATE,
    BODY_AGGREGATE_ELEMENT,
    BODY_AGGREGATE,
    HEAD_AGGREGATE_ELEMENT,
    HEAD_AGGREGATE,
    DISJUNCTION,
    DISJOINT_ELEMENT,
    DISJOINT,
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

}
