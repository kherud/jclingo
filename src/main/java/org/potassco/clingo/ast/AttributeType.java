package org.potassco.clingo.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration of attributes types used by the AST.
 * @author Josef Schneeberger
 */
public enum AttributeType {
	/** For an attribute of type "int". */
	NUMBER(0),
	/** For an attribute of type "clingo_ast_symbol_t". */
	SYMBOL(1),
	/** For an attribute of type "clingo_location_t". */
	LOCATION(2),
	/** For an attribute of type "char const *". */
	STRING(3),
	/** For an attribute of type "clingo_ast_t *". */
	AST(4),
	/** For an attribute of type "clingo_ast_t *" that can be NULL. */
	OPTIONAL_AST(5),
	/** For an attribute of type "char const **". */
	STRING_ARRAY(6),
	/** For an attribute of type "clingo_ast_t **". */
	AST_ARRAY(7);

    private static final Map<Integer, AttributeType> mapping = new HashMap<>();

	static {
	    for (AttributeType solveEventType : AttributeType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}

	public static AttributeType fromValue(int type) {
		return Objects.requireNonNull(mapping.get(type));
	}

    private final int type;

    AttributeType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
