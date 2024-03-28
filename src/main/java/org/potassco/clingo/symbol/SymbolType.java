package org.potassco.clingo.symbol;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration of available symbol types.
 *
 * @author Josef Schneeberger
 */
public enum SymbolType {
    /**
     * the <code>#inf</code> symbol
     */
    INFIMUM(0),
    /**
     * a numeric symbol, e.g., <code>1</code>
     */
    NUMBER(1),
    /**
     * a string symbol, e.g., <code>"a"</code>
     */
    STRING(4),
    /**
     * a numeric symbol, e.g., <code>c</code>, <code>(1, "a")</code>, or <code>f(1,"a")</code>
     */
    FUNCTION(5),
    /**
     * the <code>#sup</code> symbol
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
        return Objects.requireNonNull(mapping.get(type));
    }

    private final int type;

    SymbolType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
