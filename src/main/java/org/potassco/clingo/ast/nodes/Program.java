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
import org.potassco.clingo.ast.AstSequence;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;

public class Program extends Ast {

    public Program(Pointer ast) {
        super(ast);
    }

    public Program(Location location, String name, Ast[] parameters) {
        super(create(location, name, parameters));
    }

    public Program(Location location, String name, AstSequence parameters) {
        super(create(location, name, parameters));
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

    public AstSequence getParameters() {
        return new AstSequence(ast, AstAttribute.PARAMETERS);
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, AstAttribute.LOCATION.ordinal(), location));
    }

    public void setName(String name) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_string(ast, AstAttribute.NAME.ordinal(), name));
    }

    public void setParameters(AstSequence parameters) {
        new AstSequence(ast, AstAttribute.PARAMETERS).set(parameters);
    }

    private static Pointer create(Location location, String name, Ast[] parameters) {
        Pointer[] parameterPointers = new Pointer[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            parameterPointers[i] = parameters[i].getPointer();
        }
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.PROGRAM.ordinal(), pointerByReference, location, name, parameterPointers, new NativeSize(parameters.length)));
        return pointerByReference.getValue();
    }

    private static Pointer create(Location location, String name, AstSequence parameters) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.PROGRAM.ordinal(), pointerByReference, location, name, parameters.getPointer(), new NativeSize(parameters.size())));
        return pointerByReference.getValue();
    }

}
