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
import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstAttribute;
import org.potassco.clingo.ast.AstSequence;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;

public class BodyAggregateElement extends Ast {

    public BodyAggregateElement(Pointer ast) {
        super(ast);
    }

    public BodyAggregateElement(AstSequence terms, AstSequence condition) {
        super(create(terms, condition));
    }

    public AstSequence getTerms() {
        return new AstSequence(ast, AstAttribute.TERMS);
    }

    public AstSequence getCondition() {
        return new AstSequence(ast, AstAttribute.CONDITION);
    }

    public void setTerms(AstSequence terms) {
        new AstSequence(ast, AstAttribute.TERMS).set(terms);
    }

    public void setCondition(AstSequence condition) {
        new AstSequence(ast, AstAttribute.CONDITION).set(condition);
    }

    private static Pointer create(AstSequence terms, AstSequence condition) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.BODY_AGGREGATE_ELEMENT.ordinal(), pointerByReference, terms.getPointer(), new NativeSize(terms.size()), condition.getPointer(), new NativeSize(condition.size())));
        return pointerByReference.getValue();
    }

}
