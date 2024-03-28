package org.potassco.clingo.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration of attributes used by the AST.
 */
public enum AstAttribute {

    ARGUMENT,
    ARGUMENTS,
    ARITY,
    ATOM,
    ATOMS,
    ATOM_TYPE,
    BIAS,
    BODY,
    CODE,
    COEFFICIENT,
    COMPARISON,
    CONDITION,
    ELEMENTS,
    EXTERNAL,
    EXTERNAL_TYPE,
    FUNCTION,
    GUARD,
    GUARDS,
    HEAD,
    IS_DEFAULT,
    LEFT,
    LEFT_GUARD,
    LITERAL,
    LOCATION,
    MODIFIER,
    NAME,
    NODE_U,
    NODE_V,
    OPERATOR_NAME,
    OPERATOR_TYPE,
    OPERATORS,
    PARAMETERS,
    POSITIVE,
    PRIORITY,
    RIGHT,
    RIGHT_GUARD,
    SEQUENCE_TYPE,
    SIGN,
    SYMBOL,
    TERM,
    TERMS,
    VALUE,
    VARIABLE,
    WEIGHT,
    COMMENT_TYPE;

    private static final Map<Integer, AstAttribute> mapping = new HashMap<>();

    static {
        for (AstAttribute type : AstAttribute.values()) {
            mapping.put(
                    type.ordinal(),
                    type
            );
        }
    }

    public static AstAttribute fromOrdinal(int attribute) {
        return Objects.requireNonNull(mapping.get(attribute));
    }

}
