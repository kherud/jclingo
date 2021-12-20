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

public class BodyAggregate extends Ast {

    public BodyAggregate(Pointer ast) {
        super(ast);
    }
    
    public BodyAggregate(Location location, Ast leftGuard, int function, AstSequence elements, Ast rightGuard) {
        super(create(location, leftGuard, function, elements, rightGuard));
    }
    
    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, Attribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public Ast getLeftGuard() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_optional_ast(ast, Attribute.LEFT_GUARD.ordinal(), pointerByReference));
        if (pointerByReference.getValue() == null)
            throw new NoSuchElementException("there is no optional ast");
        return Ast.create(pointerByReference.getValue());
    }

    public int getFunction() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, Attribute.FUNCTION.ordinal(), intByReference));
        return intByReference.getValue();
    }

    public AstSequence getElements() {
        return new AstSequence(ast, Attribute.ELEMENTS);
    }

    public Ast getRightGuard() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_optional_ast(ast, Attribute.RIGHT_GUARD.ordinal(), pointerByReference));
        if (pointerByReference.getValue() == null)
            throw new NoSuchElementException("there is no optional ast");
        return Ast.create(pointerByReference.getValue());
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, Attribute.LOCATION.ordinal(), location));
    }

    public void setLeftGuard(Ast leftGuard) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_optional_ast(this.ast, Attribute.LEFT_GUARD.ordinal(), leftGuard.getPointer()));
    }

    public void setFunction(int function) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, Attribute.FUNCTION.ordinal(), function));
    }

    public void setElements(AstSequence elements) {
        new AstSequence(ast, Attribute.ELEMENTS).set(elements);
    }

    public void setRightGuard(Ast rightGuard) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_optional_ast(this.ast, Attribute.RIGHT_GUARD.ordinal(), rightGuard.getPointer()));
    }
    
    private static Pointer create(Location location, Ast leftGuard, int function, AstSequence elements, Ast rightGuard) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.BODY_AGGREGATE.ordinal(), pointerByReference, location, leftGuard.getPointer(), function, elements.getPointer(), new NativeSize(elements.size()), rightGuard.getPointer()));
        return pointerByReference.getValue();
    }

}