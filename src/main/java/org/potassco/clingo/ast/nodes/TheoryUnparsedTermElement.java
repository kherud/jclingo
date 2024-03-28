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
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.StringSequence;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;

public class TheoryUnparsedTermElement extends Ast {

    public TheoryUnparsedTermElement(Pointer ast) {
        super(ast);
    }

    public TheoryUnparsedTermElement(String[] operators, Ast term) {
        super(create(operators, term));
    }

    public TheoryUnparsedTermElement(StringSequence operators, Ast term) {
        super(create(operators, term));
    }

    public StringSequence getOperators() {
        return new StringSequence(ast, AstAttribute.OPERATORS);
    }

    public Ast getTerm() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, AstAttribute.TERM.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public void setOperators(StringSequence operators) {
        new StringSequence(ast, AstAttribute.OPERATORS).set(operators);
    }

    public void setTerm(Ast term) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, AstAttribute.TERM.ordinal(), term.getPointer()));
    }

    private static Pointer create(String[] operators, Ast term) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.THEORY_UNPARSED_TERM_ELEMENT.ordinal(), pointerByReference, operators, new NativeSize(operators.length), term.getPointer()));
        return pointerByReference.getValue();
    }

    private static Pointer create(StringSequence operators, Ast term) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.THEORY_UNPARSED_TERM_ELEMENT.ordinal(), pointerByReference, operators.get(), new NativeSize(operators.size()), term.getPointer()));
        return pointerByReference.getValue();
    }

}
