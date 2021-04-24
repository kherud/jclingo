package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of unary operators.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_unary_operator_e}
 */
//! Enumeration of unary operators.
/* enum clingo_ast_unary_operator_e {
  clingo_ast_unary_operator_minus    = 0, //!< Operator "-".
  clingo_ast_unary_operator_negation = 1, //!< Operator "~".
  clingo_ast_unary_operator_absolute = 2  //!< Operator "|.|".
}; */
public enum UnaryOperator {

    ABSOLUTE(2,"|","|"),
    MINUS(0,"-",""),
    NEGATION(1,"~","");
            
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
