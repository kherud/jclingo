package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Supported check modes for propagators.
 * <p>Note that total checks are subject to the lock when a model is found.
 * This means that information from previously found models can be used to discard assignments in check calls.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_propagator_check_mode_e}
 */
public enum PropagatorCheckMode {
	/** do not call @ref ::clingo_propagator::check() at all */
    NONE(0),
	/** call @ref ::clingo_propagator::check() on total assignments */
    TOTAL(1),
	/** call @ref ::clingo_propagator::check() on propagation fixpoints */
    PARTIAL(2),
	/** call @ref ::clingo_propagator::check() on propagation fixpoints and total assignments */
    BOTH(3);

    private int mode;

    private PropagatorCheckMode(int mode) {
        this.mode = mode;
    }

    public int getValue() {
        return mode;
    }

}
