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

public class TheorySequence extends Ast {

    public TheorySequence(Pointer ast) {
        super(ast);
    }
    
    public TheorySequence(Location location, int sequenceType, AstSequence terms) {
        super(create(location, sequenceType, terms));
    }
    
    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, Attribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public int getSequenceType() {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_number(ast, Attribute.SEQUENCE_TYPE.ordinal(), intByReference));
        return intByReference.getValue();
    }

    public AstSequence getTerms() {
        return new AstSequence(ast, Attribute.TERMS);
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, Attribute.LOCATION.ordinal(), location));
    }

    public void setSequenceType(int sequenceType) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_number(ast, Attribute.SEQUENCE_TYPE.ordinal(), sequenceType));
    }

    public void setTerms(AstSequence terms) {
        new AstSequence(ast, Attribute.TERMS).set(terms);
    }
    
    private static Pointer create(Location location, int sequenceType, AstSequence terms) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.THEORY_SEQUENCE.ordinal(), pointerByReference, location, sequenceType, terms.getPointer(), new NativeSize(terms.size())));
        return pointerByReference.getValue();
    }

}