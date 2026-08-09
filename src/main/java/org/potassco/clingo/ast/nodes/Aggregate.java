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

public class Aggregate extends Ast {

    public Aggregate(MemorySegment ast) {
        super(ast);
    }

    public Aggregate(Location location, Ast leftGuard, AstSequence elements, Ast rightGuard) {
        super(create(location, leftGuard, elements, rightGuard));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public boolean hasLeftGuard() {
        return hasOptionalAst(AstAttribute.LEFT_GUARD);
    }

    public Ast getLeftGuard() {
        return getOptionalAst(AstAttribute.LEFT_GUARD);
    }

    public AstSequence getElements() {
        return new AstSequence(this, AstAttribute.ELEMENTS);
    }

    public boolean hasRightGuard() {
        return hasOptionalAst(AstAttribute.RIGHT_GUARD);
    }

    public Ast getRightGuard() {
        return getOptionalAst(AstAttribute.RIGHT_GUARD);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setLeftGuard(Ast leftGuard) {
        setOptionalAst(AstAttribute.LEFT_GUARD, leftGuard);
    }

    public void setElements(AstSequence elements) {
        new AstSequence(this, AstAttribute.ELEMENTS).set(elements);
    }

    public void setRightGuard(Ast rightGuard) {
        setOptionalAst(AstAttribute.RIGHT_GUARD, rightGuard);
    }

    private static MemorySegment create(Location location, Ast leftGuard, AstSequence elements, Ast rightGuard) {
        return build(AstType.AGGREGATE, location, leftGuard, elements, rightGuard);
    }

}
