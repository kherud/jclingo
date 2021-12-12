package org.potassco.clingo.ast;

import com.sun.jna.Pointer;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.args.AttributeType;

public class AstSequence implements ErrorChecking {

    private final Pointer ast;
    private final AttributeType attributeType = AttributeType.AST_ARRAY;

    public AstSequence(Pointer ast) {
        this.ast = ast;
    }
}
