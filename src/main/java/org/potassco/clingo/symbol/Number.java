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

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;

public class Number extends Symbol {

    private final int number;

    protected Number(long symbol) {
        super(symbol);
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_number(symbol, intByReference));
        this.number = intByReference.getValue();
    }

    public Number(int number) {
        super(Number.create(number));
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    private static long create(int number) {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_symbol_create_number(number, longByReference);
        return longByReference.getValue();
    }
}
