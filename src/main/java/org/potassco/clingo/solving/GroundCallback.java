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
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.symbol.Symbol;

/**
 * Callback function to implement external functions.
 * <p>
 * If an external function of form <tt>\@name(parameters)</tt> occurs in a logic program,
 * then this function is called with its location, name, parameters, and a callback to inject symbols as arguments.
 * The callback can be called multiple times; all symbols passed are injected.
 * <p>
 * If a (non-recoverable) clingo API function fails in this callback, for example, the symbol callback, the callback must return false.
 * In case of errors not related to clingo, this function can set error ::clingo_error_unknown and return false to stop grounding with an error.
 */
public interface GroundCallback extends Callback {
    /**
     * @param locationPointer    location from which the external function was called
     * @param name               name of the called external function
     * @param argumentsPointer   arguments of the called external function
     * @param argumentsSize      number of arguments
     * @param data               user data of the callback
     * @param symbolCallback     function to inject symbols
     * @param symbolCallbackData user data for the symbol callback (must be passed untouched)
     * @return whether the call was successful
     */
    default boolean callback(
            Pointer locationPointer,
            String name,
            Pointer argumentsPointer,
            NativeSize argumentsSize,
            Pointer data,
            SymbolCallback symbolCallback,
            Pointer symbolCallbackData) {
        int size = argumentsSize.intValue();
        long[] argumentLongs = size == 0 ? new long[0] : argumentsPointer.getLongArray(0, size);
        Symbol[] arguments = new Symbol[size];
        for (int i = 0; i < size; i++)
            arguments[i] = Symbol.fromLong(argumentLongs[0]);
        Location location = new Location(locationPointer);
        call(location, name, arguments, symbolCallback);
        return true;
    }

    /**
     * Callback function to implement external functions.
     *
     * @param location       location from which the external function was called
     * @param name           name of the called external function
     * @param arguments      arguments of the called external function
     * @param symbolCallback function to inject symbols
     */
    void call(Location location, String name, Symbol[] arguments, SymbolCallback symbolCallback);
}
