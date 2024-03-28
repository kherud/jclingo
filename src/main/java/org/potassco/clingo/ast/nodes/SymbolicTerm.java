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
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.ast.*;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.symbol.Symbol;

public class SymbolicTerm extends Ast {

    public SymbolicTerm(Pointer ast) {
        super(ast);
    }

    public SymbolicTerm(Location location, Symbol symbol) {
        super(create(location, symbol));
    }

    public Location getLocation() {
        Location.ByReference locationByReference = new Location.ByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_location(ast, AstAttribute.LOCATION.ordinal(), locationByReference));
        return locationByReference;
    }

    public Symbol getSymbol() {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_get_symbol(ast, AstAttribute.SYMBOL.ordinal(), longByReference));
        return Symbol.fromLong(longByReference.getValue());
    }

    public void setLocation(Location location) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_location(ast, AstAttribute.LOCATION.ordinal(), location));
    }

    public void setSymbol(Symbol symbol) {
        Clingo.check(Clingo.INSTANCE.clingo_ast_attribute_set_symbol(ast, AstAttribute.SYMBOL.ordinal(), symbol.getLong()));
    }

    private static Pointer create(Location location, Symbol symbol) {
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_ast_build(AstType.SYMBOLIC_TERM.ordinal(), pointerByReference, location, symbol.getLong()));
        return pointerByReference.getValue();
    }

}
