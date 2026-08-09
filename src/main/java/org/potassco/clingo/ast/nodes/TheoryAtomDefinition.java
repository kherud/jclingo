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
import org.potassco.clingo.ast.AstType;
import org.potassco.clingo.ast.Location;

public class TheoryAtomDefinition extends Ast {

    public TheoryAtomDefinition(MemorySegment ast) {
        super(ast);
    }

    public TheoryAtomDefinition(Location location, int atomType, String name, int arity, String term, Ast guard) {
        super(create(location, atomType, name, arity, term, guard));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public int getAtomType() {
        return getNumber(AstAttribute.ATOM_TYPE);
    }

    public String getName() {
        return getString(AstAttribute.NAME);
    }

    public int getArity() {
        return getNumber(AstAttribute.ARITY);
    }

    public String getTerm() {
        return getString(AstAttribute.TERM);
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

    public void setAtomType(int atomType) {
        setNumber(AstAttribute.ATOM_TYPE, atomType);
    }

    public void setName(String name) {
        setString(AstAttribute.NAME, name);
    }

    public void setArity(int arity) {
        setNumber(AstAttribute.ARITY, arity);
    }

    public void setTerm(String term) {
        setString(AstAttribute.TERM, term);
    }

    public void setGuard(Ast guard) {
        setOptionalAst(AstAttribute.GUARD, guard);
    }

    private static MemorySegment create(Location location, int atomType, String name, int arity, String term, Ast guard) {
        return build(AstType.THEORY_ATOM_DEFINITION, location, atomType, name, arity, term, guard);
    }

}
