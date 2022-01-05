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
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.symbol.Symbol;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
public abstract class GroundCallback implements Callback {

    private Clingo.SymbolCallback symbolCallback;
    private Pointer symbolCallbackData;

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
    public boolean callback(
            Pointer locationPointer,
            String name,
            Pointer argumentsPointer,
            NativeSize argumentsSize,
            Pointer data,
            Clingo.SymbolCallback symbolCallback,
            Pointer symbolCallbackData) {

        this.symbolCallback = symbolCallback;
        this.symbolCallbackData = symbolCallbackData;

        int size = argumentsSize.intValue();

        // create array of method argument types including the position in the file, e. g. for add(1, 1)
        // [Location.class, Symbol.class, Symbol.class]
        Class<?>[] parameterTypes = new Class[1 + size];
        parameterTypes[0] = Location.class;
        for (int i = 0; i < size; i++) {
            parameterTypes[1 + i] = Symbol.class;
        }

        // lookup the method
        Method method = findMethod(name, parameterTypes);

        // if it was found, invoke the method and return early
        if (method != null) {
            Object[] args = new Object[1 + size];
            args[0] = new Location(locationPointer);
            long[] symbols = size == 0 ? new long[0] : argumentsPointer.getLongArray(0, size);
            for (int i = 0; i < symbols.length; i++) {
                args[1 + i] = Symbol.fromLong(symbols[i]);
            }
            return invokeMethod(method, args);
        }

        // else, if it cannot be found, try to find the method without the location argument
        Class<?>[] reducedParameterTypes = new Class<?>[parameterTypes.length - 1];
        System.arraycopy(parameterTypes, 1, reducedParameterTypes, 0, reducedParameterTypes.length);
        method = findMethod(name, reducedParameterTypes);

        // throw error if there is still no method
        if (method == null) {
            StringBuilder description = new StringBuilder();
            description.append(name);
            description.append("(");
            for (int i = 0; i < size; i++) {
                description.append("Symbol symbol");
                if (i < size - 1)
                    description.append(", ");
            }
            description.append(")");
            throw new IllegalStateException("grounding callback has no public method '" + description + "'");
        }

        // else invoke the function

        Object[] args = new Object[size];
        long[] symbols = size == 0 ? new long[0] : argumentsPointer.getLongArray(0, size);
        for (int i = 0; i < symbols.length; i++) {
            args[i] = Symbol.fromLong(symbols[i]);
        }
        return invokeMethod(method, args);
    }

    private Method findMethod(String name, Class<?>[] parameterTypes) {
        Method method = null;
        try {
            method = this.getClass().getMethod(name, parameterTypes);
        } catch (SecurityException e) {
            throw new IllegalStateException("your platform does not support grounding callbacks");
        } catch (NoSuchMethodException ignored) {
        }

        try {
            Class<?> returnType = method.getReturnType();
            assert returnType == void.class || returnType == Symbol.class;
        } catch (AssertionError err) {
            throw new IllegalStateException("'" + method.getName() + "' returns something different than a Symbol");
        } catch (NullPointerException ignored) {

        }

        return method;
    }

    private boolean invokeMethod(Method method, Object[] args) {
        try {
            method.setAccessible(true);
            Object ret = method.invoke(this, args);

            if (ret instanceof Symbol) {
                addSymbols((Symbol) ret);
            }

            if (ret instanceof Symbol[]) {
                addSymbols((Symbol[]) ret);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not invoke method '" + method.getName() + "'");
        }
        return true;
    }

    /**
     * Inject symbols in the program.
     *
     * @param symbols the symbols to inject
     */
    public void addSymbols(Symbol... symbols) {
        long[] symbolLongs = new long[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            symbolLongs[i] = symbols[i].getLong();
        }
        NativeSize size = new NativeSize(symbols.length);
        symbolCallback.callback(symbolLongs, size, symbolCallbackData);
    }
}
