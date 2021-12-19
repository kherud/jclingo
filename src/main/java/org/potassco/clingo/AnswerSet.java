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

package org.potassco.clingo;

import org.potassco.clingo.solving.ModelType;
import org.potassco.clingo.symbol.Symbol;

import java.util.List;

public class AnswerSet {

    private final List<Symbol> symbols;
    private final long[] cost;
    private final ModelType type;

    public AnswerSet(List<Symbol> symbols, ModelType type, long[] cost) {
        this.symbols = symbols;
        this.type = type;
        this.cost = cost;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public long[] getCost() {
        return cost;
    }

    public ModelType getType() {
        return type;
    }
}
