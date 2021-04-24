package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of available symbol types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_symbol_type_e}
 */
public enum SymbolType {
    /** the <tt>\#inf</tt> symbol */
    INFIMUM(0),
    /** a numeric symbol, e.g., `1` */
    NUMBER(1),
    /** a string symbol, e.g., `"a"` */
    STRING(4),
    /** a numeric symbol, e.g., `c`, `(1, "a")`, or `f(1,"a")` */
    FUNCTION(5),
    /** the <tt>\#sup</tt> symbol */
    SUPREMUM(7);
    
    private int type;
        
    private SymbolType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
