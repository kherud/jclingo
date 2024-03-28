package org.potassco.clingo.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration for entries of the statistics.
 *
 * @author Josef Schneeberger
 */
public enum StatisticsType {
    /**
     * the entry is invalid (has neither of the types below)
     */
    EMPTY(0),
    /**
     * the entry is a (double) value
     */
    VALUE(1),
    /**
     * the entry is an array
     */
    ARRAY(2),
    /**
     * the entry is a map
     */
    MAP(3);

    private static final Map<Integer, StatisticsType> mapping = new HashMap<>();

    static {
        for (StatisticsType solveEventType : StatisticsType.values()) {
            mapping.put(
                    solveEventType.getValue(),
                    solveEventType
            );
        }
    }

    public static StatisticsType fromValue(int type) {
        return Objects.requireNonNull(mapping.get(type));
    }

    private final int type;

    StatisticsType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
