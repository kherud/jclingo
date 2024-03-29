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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstAttribute;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.internal.Clingo;

public class AggregateGuard extends Ast {

    public AggregateGuard(Pointer ast) {
        super(ast);
    }

    public AggregateGuard(int comparison, Ast term) {
        super(create(comparison, term));
    }

    public int getComparison() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, AstAttribute.COMPARISON.ordinal(), intByReference));
        return intByReference.getValue();
    }

    public Ast getTerm() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, AstAttribute.TERM.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public void setComparison(int comparison) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, AstAttribute.COMPARISON.ordinal(), comparison));
    }

    public void setTerm(Ast term) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, AstAttribute.TERM.ordinal(), term.getPointer()));
    }

    private static Pointer create(int comparison, Ast term) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.AGGREGATE_GUARD.ordinal(), pointerByReference, comparison, term.getPointer()));
        return pointerByReference.getValue();
    }

}
