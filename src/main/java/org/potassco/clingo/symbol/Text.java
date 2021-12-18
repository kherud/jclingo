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

public class Text extends Symbol {

    private final String text;

    protected Text(long symbol) {
        super(symbol);
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_symbol_string(symbol, stringByReference));
        this.text = stringByReference[0];
    }

    public Text(String text) {
        super(Text.create(text));
        this.text = text;
    }

    public String getText() {
        return text;
    }

    private static long create(String text) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_create_string(text, longByReference));
        return longByReference.getValue();
    }
}
