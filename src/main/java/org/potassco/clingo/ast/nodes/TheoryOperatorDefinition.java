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
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.Location;

public class TheoryOperatorDefinition extends Ast {

    public TheoryOperatorDefinition(MemorySegment ast) {
        super(ast);
    }

    public TheoryOperatorDefinition(Location location, String name, int priority, int operatorType) {
        super(create(location, name, priority, operatorType));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public String getName() {
        return getString(AstAttribute.NAME);
    }

    public int getPriority() {
        return getNumber(AstAttribute.PRIORITY);
    }

    public int getOperatorType() {
        return getNumber(AstAttribute.OPERATOR_TYPE);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setName(String name) {
        setString(AstAttribute.NAME, name);
    }

    public void setPriority(int priority) {
        setNumber(AstAttribute.PRIORITY, priority);
    }

    public void setOperatorType(int operatorType) {
        setNumber(AstAttribute.OPERATOR_TYPE, operatorType);
    }

    private static MemorySegment create(Location location, String name, int priority, int operatorType) {
        return build(AstType.THEORY_OPERATOR_DEFINITION, location, name, priority, operatorType);
    }

}
