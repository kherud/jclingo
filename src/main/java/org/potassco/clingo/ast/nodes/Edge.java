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

public class Edge extends Ast {

    public Edge(MemorySegment ast) {
        super(ast);
    }

    public Edge(Location location, Ast nodeU, Ast nodeV, AstSequence body) {
        super(create(location, nodeU, nodeV, body));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public Ast getNodeU() {
        return getAst(AstAttribute.NODE_U);
    }

    public Ast getNodeV() {
        return getAst(AstAttribute.NODE_V);
    }

    public AstSequence getBody() {
        return new AstSequence(this, AstAttribute.BODY);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setNodeU(Ast nodeU) {
        setAst(AstAttribute.NODE_U, nodeU);
    }

    public void setNodeV(Ast nodeV) {
        setAst(AstAttribute.NODE_V, nodeV);
    }

    public void setBody(AstSequence body) {
        new AstSequence(this, AstAttribute.BODY).set(body);
    }

    private static MemorySegment create(Location location, Ast nodeU, Ast nodeV, AstSequence body) {
        return build(AstType.EDGE, location, nodeU, nodeV, body);
    }

}
