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

public class Definition extends Ast {

    public Definition(MemorySegment ast) {
        super(ast);
    }

    public Definition(Location location, String name, Ast value, int isDefault) {
        super(create(location, name, value, isDefault));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public String getName() {
        return getString(AstAttribute.NAME);
    }

    public Ast getValue() {
        return getAst(AstAttribute.VALUE);
    }

    public int getIsDefault() {
        return getNumber(AstAttribute.IS_DEFAULT);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setName(String name) {
        setString(AstAttribute.NAME, name);
    }

    public void setValue(Ast value) {
        setAst(AstAttribute.VALUE, value);
    }

    public void setIsDefault(int isDefault) {
        setNumber(AstAttribute.IS_DEFAULT, isDefault);
    }

    private static MemorySegment create(Location location, String name, Ast value, int isDefault) {
        return build(AstType.DEFINITION, location, name, value, isDefault);
    }

}
