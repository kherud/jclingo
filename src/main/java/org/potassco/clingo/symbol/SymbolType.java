package org.potassco.clingo.symbol;


import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of available symbol types.
 *
 * @author Josef Schneeberger
 */
public enum SymbolType {
    /**
     * the <tt>\#inf</tt> symbol
     */
    INFIMUM(0),
    /**
     * a numeric symbol, e.g., `1`
     */
    NUMBER(1),
    /**
     * a string symbol, e.g., `"a"`
     */
    STRING(4),
    /**
     * a numeric symbol, e.g., `c`, `(1, "a")`, or `f(1,"a")`
     */
    FUNCTION(5),
    /**
     * the <tt>\#sup</tt> symbol
     */
    SUPREMUM(7);

    private static final Map<Integer, SymbolType> mapping = new HashMap<>();

    static {
        for (SymbolType solveEventType : SymbolType.values()) {
            mapping.put(
                    solveEventType.getValue(),
                    solveEventType
            );
        }
    }

    public static SymbolType fromValue(int type) {
        return mapping.get(type);
    }

    private final int type;

    SymbolType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
