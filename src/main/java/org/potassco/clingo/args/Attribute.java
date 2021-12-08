package org.potassco.clingo.args;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of attributes used by the AST.
 * @author Josef Schneeberger
 */
public enum Attribute {ARGUMENT,
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
	  CSP,
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
	  SCRIPT_TYPE,
	  SEQUENCE_TYPE,
	  SIGN,
	  SYMBOL,
	  TERM,
	  TERMS,
	  VALUE,
	  VARIABLE,
	  WEIGHT;

    private static final Map<Integer, Attribute> mapping = new HashMap<>();

	static {
	    for (Attribute type : Attribute.values()) {
	    	mapping.put(
	          type.ordinal(),
	          type
	        );
	    }
	}

	public static Attribute fromOrdinal(int attribute) {
		return mapping.get(attribute);
	}

}
