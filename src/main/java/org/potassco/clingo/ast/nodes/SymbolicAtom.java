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

public class SymbolicAtom extends Ast {

    public SymbolicAtom(MemorySegment ast) {
        super(ast);
    }

    public SymbolicAtom(Ast symbol) {
        super(create(symbol));
    }

    public Ast getSymbol() {
        return getAst(AstAttribute.SYMBOL);
    }

    public void setSymbol(Ast symbol) {
        setAst(AstAttribute.SYMBOL, symbol);
    }

    private static MemorySegment create(Ast symbol) {
        return build(AstType.SYMBOLIC_ATOM, symbol);
    }

}
