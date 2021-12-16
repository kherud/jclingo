package org.potassco.clingo.ast;

import com.sun.jna.Pointer;

public class AstSequence {

    private final Pointer ast;
    private final AttributeType attributeType = AttributeType.AST_ARRAY;

    public AstSequence(Pointer ast) {
        this.ast = ast;
    }
}
