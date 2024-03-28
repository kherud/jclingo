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

public class Defined extends Ast {

    public Defined(Pointer ast) {
        super(ast);
    }

    public Defined(Location location, String name, int arity, int positive) {
        super(create(location, name, arity, positive));
    }

    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, AstAttribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public String getName() {
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_string(ast, AstAttribute.NAME.ordinal(), stringByReference));
        return stringByReference[0];
    }

    public int getArity() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, AstAttribute.ARITY.ordinal(), intByReference));
        return intByReference.getValue();
    }

    public int getPositive() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, AstAttribute.POSITIVE.ordinal(), intByReference));
        return intByReference.getValue();
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, AstAttribute.LOCATION.ordinal(), location));
    }

    public void setName(String name) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_string(ast, AstAttribute.NAME.ordinal(), name));
    }

    public void setArity(int arity) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, AstAttribute.ARITY.ordinal(), arity));
    }

    public void setPositive(int positive) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, AstAttribute.POSITIVE.ordinal(), positive));
    }

    private static Pointer create(Location location, String name, int arity, int positive) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.DEFINED.ordinal(), pointerByReference, location, name, arity, positive));
        return pointerByReference.getValue();
    }

}
