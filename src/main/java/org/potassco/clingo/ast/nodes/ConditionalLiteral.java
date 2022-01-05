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
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.symbol.Symbol;

import java.util.NoSuchElementException;

public class ConditionalLiteral extends Ast {

    public ConditionalLiteral(Pointer ast) {
        super(ast);
    }
    
    public ConditionalLiteral(Location location, Ast literal, AstSequence condition) {
        super(create(location, literal, condition));
    }
    
    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, Attribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public Ast getLiteral() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, Attribute.LITERAL.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public AstSequence getCondition() {
        return new AstSequence(ast, Attribute.CONDITION);
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, Attribute.LOCATION.ordinal(), location));
    }

    public void setLiteral(Ast literal) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, Attribute.LITERAL.ordinal(), literal.getPointer()));
    }

    public void setCondition(AstSequence condition) {
        new AstSequence(ast, Attribute.CONDITION).set(condition);
    }
    
    private static Pointer create(Location location, Ast literal, AstSequence condition) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.CONDITIONAL_LITERAL.ordinal(), pointerByReference, location, literal.getPointer(), condition.getPointer(), new NativeSize(condition.size())));
        return pointerByReference.getValue();
    }

}
