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

public class Edge extends Ast {

    public Edge(Pointer ast) {
        super(ast);
    }
    
    public Edge(Location location, Ast nodeU, Ast nodeV, AstSequence body) {
        super(create(location, nodeU, nodeV, body));
    }
    
    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, Attribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public Ast getNodeU() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, Attribute.NODE_U.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public Ast getNodeV() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, Attribute.NODE_V.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public AstSequence getBody() {
        return new AstSequence(ast, Attribute.BODY);
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, Attribute.LOCATION.ordinal(), location));
    }

    public void setNodeU(Ast nodeU) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, Attribute.NODE_U.ordinal(), nodeU.getPointer()));
    }

    public void setNodeV(Ast nodeV) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, Attribute.NODE_V.ordinal(), nodeV.getPointer()));
    }

    public void setBody(AstSequence body) {
        new AstSequence(ast, Attribute.BODY).set(body);
    }
    
    private static Pointer create(Location location, Ast nodeU, Ast nodeV, AstSequence body) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.EDGE.ordinal(), pointerByReference, location, nodeU.getPointer(), nodeV.getPointer(), body.getPointer(), new NativeSize(body.size())));
        return pointerByReference.getValue();
    }

}