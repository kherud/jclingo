package org.potassco.clingo.ast;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

// Callback function to intercept AST nodes.
public interface AstCallback  extends Callback {
    /**
     * @param ast the AST
     * @param data a user data pointer
     * @return whether the call was successful
     */
    default boolean callback(Pointer ast, Pointer data) {
        call(new Ast(ast));
        return true;
    }

    void call(Ast ast);

}
