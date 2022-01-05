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

public class BinaryOperation extends Ast {

    public BinaryOperation(Pointer ast) {
        super(ast);
    }

    public BinaryOperation(Location location, int operatorType, Ast left, Ast right) {
        super(create(location, operatorType, left, right));
    }

    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, Attribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public int getOperatorType() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, Attribute.OPERATOR_TYPE.ordinal(), intByReference));
        return intByReference.getValue();
    }

    public Ast getLeft() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, Attribute.LEFT.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public Ast getRight() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, Attribute.RIGHT.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, Attribute.LOCATION.ordinal(), location));
    }

    public void setOperatorType(int operatorType) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, Attribute.OPERATOR_TYPE.ordinal(), operatorType));
    }

    public void setLeft(Ast left) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, Attribute.LEFT.ordinal(), left.getPointer()));
    }

    public void setRight(Ast right) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, Attribute.RIGHT.ordinal(), right.getPointer()));
    }

    private static Pointer create(Location location, int operatorType, Ast left, Ast right) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.BINARY_OPERATION.ordinal(), pointerByReference, location, operatorType, left.getPointer(), right.getPointer()));
        return pointerByReference.getValue();
    }

}
