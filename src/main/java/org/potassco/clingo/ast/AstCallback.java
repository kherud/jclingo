package org.potassco.clingo.ast;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

// Callback function to intercept AST nodes.
public abstract class AstCallback  implements Callback {
    /**
     * @param ast the AST
     * @param data a user data pointer
     * @return whether the call was successful
     */
    public boolean callback(Pointer ast, Pointer data) {
        return call(ast, data);
    }

    public abstract boolean call(Pointer ast, Pointer data);

}
