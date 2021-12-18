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
 
package org.potassco.clingo.symbol;

import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;

public class Supremum extends Symbol {

    protected Supremum(long symbol) {
        super(symbol);
    }

    public Supremum() {
        super(Supremum.create());
    }

    public String getString() {
        return "#sup";
    }

    private static long create() {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_symbol_create_supremum(longByReference);
        return longByReference.getValue();
    }

}
