package org.potassco.enums;

import java.util.HashMap;
import java.util.Map;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of bit flags to select symbols in models.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_show_type_e}
 */
public enum ShowType {
    /** Select CSP assignments. */
    CSP(1),
    /**Select shown atoms and terms.  */
    SHOWN(2),
    /** Select all atoms. */
    ATOMS(4),
    /** Select all terms. */
    TERMS(8),
    /** Select symbols added by theory. */
    THEORY(16),
    /** Select everything. */
    ALL(31),
    /** Select false instead of true atoms (::clingo_show_type_atoms) or terms (::clingo_show_type_terms). */
    COMPLEMENT(32);

    private static Map<Integer, ShowType> mapping = new HashMap<>();
    
	static {
	    for (ShowType solveEventType : ShowType.values()) {
	    	mapping.put(
	          solveEventType.getValue(),
	          solveEventType
	        );
	    }
	}
	
	public static ShowType fromValue(int type) {
		return mapping.get(type);
	}

    private int type;

    private ShowType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }
    
}
