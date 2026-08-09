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

import java.util.NoSuchElementException;

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstAttribute;
import org.potassco.clingo.ast.AstSequence;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.Location;

public class TheoryAtom extends Ast {

    public TheoryAtom(MemorySegment ast) {
        super(ast);
    }

    public TheoryAtom(Location location, Ast term, AstSequence elements, Ast guard) {
        super(create(location, term, elements, guard));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public Ast getTerm() {
        return getAst(AstAttribute.TERM);
    }

    public AstSequence getElements() {
        return new AstSequence(this, AstAttribute.ELEMENTS);
    }

    public boolean hasGuard() {
        return hasOptionalAst(AstAttribute.GUARD);
    }

    public Ast getGuard() {
        return getOptionalAst(AstAttribute.GUARD);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setTerm(Ast term) {
        setAst(AstAttribute.TERM, term);
    }

    public void setElements(AstSequence elements) {
        new AstSequence(this, AstAttribute.ELEMENTS).set(elements);
    }

    public void setGuard(Ast guard) {
        setOptionalAst(AstAttribute.GUARD, guard);
    }

    private static MemorySegment create(Location location, Ast term, AstSequence elements, Ast guard) {
        return build(AstType.THEORY_ATOM, location, term, elements, guard);
    }

}
