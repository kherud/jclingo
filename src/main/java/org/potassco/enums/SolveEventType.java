package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of solve events.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_solve_event_type_e}
 */
public enum SolveEventType {
	/** Issued if a model is found. */
    MODEL(0),
    /** Issued if an optimization problem is found unsatisfiable. */
    UNSAT(1),
    /** Issued when the statistics can be updated. */
    STATISTICS(2),
    /** Issued if the search has completed. */
    FINISH(3);

    private final int type;

    private SolveEventType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
