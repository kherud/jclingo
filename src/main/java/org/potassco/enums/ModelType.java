package org.potassco.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration for the different model types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_model_type_e}
 */
public enum ModelType {
	/** The model represents a stable model. */
    STABLE_MODEL(0),
	/** The model represents a set of brave consequences. */
    BRAVE_CONSEQUENCES(1),
	/** The model represents a set of cautious consequences. */
    CAUTIOUS_CONSEQUENCES(2);

    private int type;

    private ModelType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }

}
