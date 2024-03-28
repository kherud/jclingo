package org.potassco.clingo.solving;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Enumeration of solve events.
 *
 * @author Josef Schneeberger
 */
public enum SolveEventType {
    /**
     * Issued if a model is found.
     */
    MODEL(0),
    /**
     * Issued if an optimization problem is found unsatisfiable.
     */
    UNSAT(1),
    /**
     * Issued when the statistics can be updated.
     */
    STATISTICS(2),
    /**
     * Issued if the search has completed.
     */
    FINISH(3);

    private static final Map<Integer, SolveEventType> mapping = new HashMap<>();

    static {
        for (SolveEventType solveEventType : SolveEventType.values()) {
            mapping.put(
                    solveEventType.getValue(),
                    solveEventType
            );
        }
    }

    public static SolveEventType fromValue(int type) {
        return Objects.requireNonNull(mapping.get(type));
    }

    private final int type;

    SolveEventType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
