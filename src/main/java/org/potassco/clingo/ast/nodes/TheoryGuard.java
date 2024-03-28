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

package org.potassco.clingo.ast.nodes;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.ast.*;
import org.potassco.clingo.internal.Clingo;

public class TheoryGuard extends Ast {

    public TheoryGuard(Pointer ast) {
        super(ast);
    }

    public TheoryGuard(String operatorName, Ast term) {
        super(create(operatorName, term));
    }

    public String getOperatorName() {
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_string(ast, AstAttribute.OPERATOR_NAME.ordinal(), stringByReference));
        return stringByReference[0];
    }

    public Ast getTerm() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, AstAttribute.TERM.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public void setOperatorName(String operatorName) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_string(ast, AstAttribute.OPERATOR_NAME.ordinal(), operatorName));
    }

    public void setTerm(Ast term) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, AstAttribute.TERM.ordinal(), term.getPointer()));
    }

    private static Pointer create(String operatorName, Ast term) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.THEORY_GUARD.ordinal(), pointerByReference, operatorName, term.getPointer()));
        return pointerByReference.getValue();
    }

}
