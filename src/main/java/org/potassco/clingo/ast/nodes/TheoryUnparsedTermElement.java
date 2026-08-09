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

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstAttribute;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.StringSequence;

public class TheoryUnparsedTermElement extends Ast {

    public TheoryUnparsedTermElement(MemorySegment ast) {
        super(ast);
    }

    public TheoryUnparsedTermElement(String[] operators, Ast term) {
        super(create(operators, term));
    }

    public TheoryUnparsedTermElement(StringSequence operators, Ast term) {
        super(create(operators, term));
    }

    public StringSequence getOperators() {
        return new StringSequence(this, AstAttribute.OPERATORS);
    }

    public Ast getTerm() {
        return getAst(AstAttribute.TERM);
    }

    public void setOperators(StringSequence operators) {
        new StringSequence(this, AstAttribute.OPERATORS).set(operators);
    }

    public void setTerm(Ast term) {
        setAst(AstAttribute.TERM, term);
    }

    private static MemorySegment create(String[] operators, Ast term) {
        return build(AstType.THEORY_UNPARSED_TERM_ELEMENT, operators, term);
    }

    private static MemorySegment create(StringSequence operators, Ast term) {
        return build(AstType.THEORY_UNPARSED_TERM_ELEMENT, operators, term);
    }

}
