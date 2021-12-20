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

public class TheoryAtom extends Ast {

    public TheoryAtom(Pointer ast) {
        super(ast);
    }
    
    public TheoryAtom(Location location, Ast term, AstSequence elements, Ast guard) {
        super(create(location, term, elements, guard));
    }
    
    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, Attribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public Ast getTerm() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_ast(ast, Attribute.TERM.ordinal(), pointerByReference));
        return Ast.create(pointerByReference.getValue());
    }

    public AstSequence getElements() {
        return new AstSequence(ast, Attribute.ELEMENTS);
    }

    public Ast getGuard() {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_optional_ast(ast, Attribute.GUARD.ordinal(), pointerByReference));
        if (pointerByReference.getValue() == null)
            throw new NoSuchElementException("there is no optional ast");
        return Ast.create(pointerByReference.getValue());
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, Attribute.LOCATION.ordinal(), location));
    }

    public void setTerm(Ast term) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_ast(ast, Attribute.TERM.ordinal(), term.getPointer()));
    }

    public void setElements(AstSequence elements) {
        new AstSequence(ast, Attribute.ELEMENTS).set(elements);
    }

    public void setGuard(Ast guard) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_optional_ast(this.ast, Attribute.GUARD.ordinal(), guard.getPointer()));
    }
    
    private static Pointer create(Location location, Ast term, AstSequence elements, Ast guard) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.THEORY_ATOM.ordinal(), pointerByReference, location, term.getPointer(), elements.getPointer(), new NativeSize(elements.size()), guard.getPointer()));
        return pointerByReference.getValue();
    }

}