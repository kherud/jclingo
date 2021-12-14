package org.potassco.clingo.ast;

import org.potassco.clingo.internal.ErrorChecking;

/**
 * Utility class to transform ASTs.
 *
 * Classes should inherit from this class and implement functions with name
 * `visit<AstType>` where `<AstType>` is the type of the ASTs to visit and
 * modify. Such a function should return an updated AST or the same AST if no
 * change is necessary. The transformer will take care to copy all parent ASTs
 * involving a modified child. Note that the class works like a visitor if
 * only self references are returned from such functions.
 *
 * Any extra arguments passed to the visit method are passed on to child ASTs.
 */
public class Transformer implements ErrorChecking {

    // TODO: implement
    public Transformer() {

    }

    /**
     * Dispatch to a visit method in a base class or visit and transform
     * the children of the given AST if it is missing.
     * @param ast the AST to visit
     * @return
     */
    public Ast visit(Ast ast) {
        return null;
    }
}