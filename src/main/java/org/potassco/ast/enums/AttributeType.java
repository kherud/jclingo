package org.potassco.ast.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of attributes types used by the AST.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_attribute_type_e}
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

    private static Map<Integer, AttributeType> mapping = new HashMap<>();
    
	static {
	    for (AttributeType solveEventType : AttributeType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	
	public static AttributeType fromValue(int type) {
		return mapping.get(type);
	}

    private int type;

    private AttributeType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
