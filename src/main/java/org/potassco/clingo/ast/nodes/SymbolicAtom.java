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
import org.potassco.clingo.ast.*;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.symbol.Symbol;

import java.util.NoSuchElementException;

public class SymbolicAtom extends Ast {

    public SymbolicAtom(Pointer ast) {
        super(ast);
    }
    
    public SymbolicAtom(Ast symbol) {
        super(create(symbol));
    }
    
    public Ast getSymbol() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, Attribute.SYMBOL.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public void setSymbol(Ast symbol) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, Attribute.SYMBOL.ordinal(), symbol.getPointer()));
    }
    
    private static Pointer create(Ast symbol) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.SYMBOLIC_ATOM.ordinal(), pointerByReference, symbol.getPointer()));
        return pointerByReference.getValue();
    }

}