package org.potassco.clingo.propagator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Supported check modes for propagators.
 * <p>
 * Note that total checks are subject to the lock when a model is found.
 * This means that information from previously found models can be used to discard assignments in check calls.
 *
 * @author Josef Schneeberger
 */
public enum PropagatorCheckMode {
    /**
     * do not call {@link Propagator#check} at all
     */
    NONE(0),
    /**
     * call {@link Propagator#check} on total assignments
     */
    TOTAL(1),
    /**
     * call {@link Propagator#check} on propagation fixpoints
     */
    FIXPOINT(2),
    /**
     * call {@link Propagator#check} on propagation fixpoints and total assignments
     */
    BOTH(3);

    private static final Map<Integer, PropagatorCheckMode> mapping = new HashMap<>();

    static {
        for (PropagatorCheckMode solveEventType : PropagatorCheckMode.values()) {
            mapping.put(
                    solveEventType.getValue(),
                    solveEventType
            );
        }
    }

    public static PropagatorCheckMode fromValue(int type) {
        return Objects.requireNonNull(mapping.get(type));
    }

    private final int mode;

    PropagatorCheckMode(int mode) {
        this.mode = mode;
    }

    public int getValue() {
        return mode;
    }

}
