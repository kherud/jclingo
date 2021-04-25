package org.potassco.ast.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of theory sequence types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_theory_sequence_type_e}
 */
public enum TheorySequenceType {
    /** Theory tuples "(t1,...,tn)". */
	TUPLE(0,"tuple"),
    /** Theory lists "[t1,...,tn]". */
	LIST(1,"list"),
    /** Theory sets "{t1,...,tn}". */
	SET(2,"set");

    private static Map<Integer, TheorySequenceType> mapping = new HashMap<>();
    
	static {
	    for (TheorySequenceType solveEventType : TheorySequenceType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	
	public static TheorySequenceType fromValue(int type) {
		return mapping.get(type);
	}

    private final int type;
    private final String string;

    private TheorySequenceType(int type, String string) {
        this.type = type;
        this.string = string;
    }

    public int getValue() {
        return type;
    }

    @Override
    public String toString() {
        return string;
    }
    
}
