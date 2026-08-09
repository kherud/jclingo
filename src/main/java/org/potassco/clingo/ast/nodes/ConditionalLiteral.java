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

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.ast.Ast;
import org.potassco.clingo.ast.AstAttribute;
import org.potassco.clingo.ast.AstSequence;
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.Location;

public class ConditionalLiteral extends Ast {

    public ConditionalLiteral(MemorySegment ast) {
        super(ast);
    }

    public ConditionalLiteral(Location location, Ast literal, AstSequence condition) {
        super(create(location, literal, condition));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public Ast getLiteral() {
        return getAst(AstAttribute.LITERAL);
    }

    public AstSequence getCondition() {
        return new AstSequence(this, AstAttribute.CONDITION);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setLiteral(Ast literal) {
        setAst(AstAttribute.LITERAL, literal);
    }

    public void setCondition(AstSequence condition) {
        new AstSequence(this, AstAttribute.CONDITION).set(condition);
    }

    private static MemorySegment create(Location location, Ast literal, AstSequence condition) {
        return build(AstType.CONDITIONAL_LITERAL, location, literal, condition);
    }

}
