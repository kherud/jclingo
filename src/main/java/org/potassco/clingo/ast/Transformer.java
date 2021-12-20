/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
 
package org.potassco.clingo.ast;

/**
 * Utility class to transform ASTs.
 * <p>
 * Classes should inherit from this class and implement functions with name
 * `visit<AstType>` where `<AstType>` is the type of the ASTs to visit and
 * modify. Such a function should return an updated AST or the same AST if no
 * change is necessary. The transformer will take care to copy all parent ASTs
 * involving a modified child. Note that the class works like a visitor if
 * only self references are returned from such functions.
 * <p>
 * Any extra arguments passed to the visit method are passed on to child ASTs.
 */
public class Transformer {

    public Ast visit(Ast ast) {
        return null;
    }

    /**
     * Dispatch to a visit method in a base class or visit and transform
     * the children of the given AST if it is missing.
     *
     * @param ast the AST to visit
     * @return
     */
//    public Ast visit(Ast ast) {
//        return null;
//    }
}
