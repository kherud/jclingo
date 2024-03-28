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

public class Literal extends Ast {

    public Literal(Pointer ast) {
        super(ast);
    }

    public Literal(Location location, int sign, Ast atom) {
        super(create(location, sign, atom));
    }

    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, AstAttribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public int getSign() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, AstAttribute.SIGN.ordinal(), intByReference));
        return intByReference.getValue();
    }

    public Ast getAtom() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, AstAttribute.ATOM.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, AstAttribute.LOCATION.ordinal(), location));
    }

    public void setSign(int sign) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, AstAttribute.SIGN.ordinal(), sign));
    }

    public void setAtom(Ast atom) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, AstAttribute.ATOM.ordinal(), atom.getPointer()));
    }

    private static Pointer create(Location location, int sign, Ast atom) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.LITERAL.ordinal(), pointerByReference, location, sign, atom.getPointer()));
        return pointerByReference.getValue();
    }

}
