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

public class AggregateGuard extends Ast {

    public AggregateGuard(MemorySegment ast) {
        super(ast);
    }

    public AggregateGuard(int comparison, Ast term) {
        super(create(comparison, term));
    }

    public int getComparison() {
        return getNumber(AstAttribute.COMPARISON);
    }

    public Ast getTerm() {
        return getAst(AstAttribute.TERM);
    }

    public void setComparison(int comparison) {
        setNumber(AstAttribute.COMPARISON, comparison);
    }

    public void setTerm(Ast term) {
        setAst(AstAttribute.TERM, term);
    }

    private static MemorySegment create(int comparison, Ast term) {
        return build(AstType.AGGREGATE_GUARD, comparison, term);
    }

}
