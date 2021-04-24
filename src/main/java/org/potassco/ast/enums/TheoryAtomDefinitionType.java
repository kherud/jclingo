package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of the theory atom types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_theory_atom_definition_type_e}
 */

public enum TheoryAtomDefinitionType {
	/** For theory atoms that can appear in the head. */
    HEAD(0, "head"),
	/** For theory atoms that can appear in the body. */
    BODY(1, "body"),
	/** For theory atoms that can appear in both head and body. */
    ANY(2, "any"),
	/** For theory atoms that must not have a body. */
    DIRECTIVE(3, "directive");

    private final int type;

    private final String string;

    private TheoryAtomDefinitionType(int type, String string) {
        this.type = type;
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    public int getValue() {
        return type;
    }
    
}
