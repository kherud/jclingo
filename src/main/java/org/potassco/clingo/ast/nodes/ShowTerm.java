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

public class ShowTerm extends Ast {

    public ShowTerm(Pointer ast) {
        super(ast);
    }

    public ShowTerm(Location location, Ast term, AstSequence body) {
        super(create(location, term, body));
    }

    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, AstAttribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public Ast getTerm() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, AstAttribute.TERM.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public AstSequence getBody() {
        return new AstSequence(ast, AstAttribute.BODY);
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, AstAttribute.LOCATION.ordinal(), location));
    }

    public void setTerm(Ast term) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, AstAttribute.TERM.ordinal(), term.getPointer()));
    }

    public void setBody(AstSequence body) {
        new AstSequence(ast, AstAttribute.BODY).set(body);
    }

    private static Pointer create(Location location, Ast term, AstSequence body) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.SHOW_TERM.ordinal(), pointerByReference, location, term.getPointer(), body.getPointer(), new NativeSize(body.size())));
        return pointerByReference.getValue();
    }

}
