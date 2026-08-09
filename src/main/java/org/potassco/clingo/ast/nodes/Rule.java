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

public class Rule extends Ast {

    public Rule(MemorySegment ast) {
        super(ast);
    }

    public Rule(Location location, Ast head, AstSequence body) {
        super(create(location, head, body));
    }

    public Location getLocation() {
        return getLocation(AstAttribute.LOCATION);
    }

    public Ast getHead() {
        return getAst(AstAttribute.HEAD);
    }

    public AstSequence getBody() {
        return new AstSequence(this, AstAttribute.BODY);
    }

    public void setLocation(Location location) {
        setLocation(AstAttribute.LOCATION, location);
    }

    public void setHead(Ast head) {
        setAst(AstAttribute.HEAD, head);
    }

    public void setBody(AstSequence body) {
        new AstSequence(this, AstAttribute.BODY).set(body);
    }

    private static MemorySegment create(Location location, Ast head, AstSequence body) {
        return build(AstType.RULE, location, head, body);
    }

}
