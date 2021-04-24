package org.potassco.ast.enums;

import org.potassco.cpp.clingo_h;

/**
 * Enumeration of script types.
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ast_script_type_e}
 */
public enum ScriptType {

    LUA(0, "lua"),
    PYTHON(1, "python");

    private int type;

    private String string;

    private ScriptType(int type, String string) {
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
