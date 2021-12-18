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
 
package org.potassco.clingo.solving;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.potassco.clingo.internal.NativeSize;

/**
 * Callback function to inject symbols.
 */
public interface SymbolCallback extends Callback {
    /**
     * @param symbols array of symbols
     * @param symbolsSize size of the symbol array
     * @param data user data of the callback
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     */
    // TODO: long[] arguments probably is not correct, rather LongByReference?
    default boolean callback(long[] symbols, NativeSize symbolsSize, Pointer data) {
        call(symbols);
        return true;
    }

    /**
     * Callback function to inject symbols.
     * @param symbols array of symbols
     */
    void call(long[] symbols);
}
