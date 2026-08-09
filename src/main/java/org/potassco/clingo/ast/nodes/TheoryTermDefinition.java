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

public class TheoryTermDefinition extends Ast {

    public TheoryTermDefinition(MemorySegment ast) {
        super(ast);
    }

    public TheoryTermDefinition(Location location, String name, AstSequence operators) {
        super(create(location, name, operators));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public String getName() {
        return getString(AstAttribute.NAME);
    }

    public AstSequence getOperators() {
        return new AstSequence(this, AstAttribute.OPERATORS);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setName(String name) {
        setString(AstAttribute.NAME, name);
    }

    public void setOperators(AstSequence operators) {
        new AstSequence(this, AstAttribute.OPERATORS).set(operators);
    }

    private static MemorySegment create(Location location, String name, AstSequence operators) {
        return build(AstType.THEORY_TERM_DEFINITION, location, name, operators);
    }

}
