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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.potassco.clingo.ast.Location;
import org.potassco.clingo.control.ErrorCode;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeArrays;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.ffi.clingo_symbol_callback_t;
import org.potassco.clingo.symbol.Symbol;

/**
 * Callback function to implement external functions.
 * <p>
 * If an external function of form <code>@name(parameters)</code> occurs in a logic program,
 * then this function is called with its location, name, parameters, and a callback to inject symbols as arguments.
 * The callback can be called multiple times; all symbols passed are injected.
 * <p>
 * An exception thrown by an external function stops grounding and is reported to clingo as
 * {@link ErrorCode#UNKNOWN}.
 */
public abstract class GroundCallback {

    // the sink to inject symbols into is only valid while clingo is calling this object, and it is held per thread so
    // that a single instance can be used by more than one control
    private final ThreadLocal<SymbolSink> symbolSink = new ThreadLocal<>();

    /**
     * @param location       location from which the external function was called
     * @param name           name of the called external function
     * @param arguments      arguments of the called external function
     * @param size           number of arguments
     * @param symbolCallback function to inject symbols
     * @param symbolData     user data for the symbol callback (must be passed untouched)
     */
    void ground(MemorySegment location, MemorySegment name, MemorySegment arguments, long size,
                MemorySegment symbolCallback, MemorySegment symbolData) {
        SymbolSink previousSink = symbolSink.get();
        symbolSink.set(new SymbolSink(symbolCallback, symbolData));
        try {
            ground(Location.fromNative(location), NativeStr.toJava(name),
                    NativeArrays.toLongs(arguments, size), (int) size);
        }
        finally {
            symbolSink.set(previousSink);
        }
    }

    private void ground(Location location, String name, long[] symbols, int size) {

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
            args[0] = location;
            for (int i = 0; i < symbols.length; i++) {
                args[1 + i] = Symbol.fromLong(symbols[i]);
            }
            invokeMethod(method, args);
            return;
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
        for (int i = 0; i < symbols.length; i++) {
            args[i] = Symbol.fromLong(symbols[i]);
        }
        invokeMethod(method, args);
    }

    private Method findMethod(String name, Class<?>[] parameterTypes) {
        Method method;
        try {
            method = this.getClass().getMethod(name, parameterTypes);
        } catch (SecurityException e) {
            throw new IllegalStateException("your platform does not support grounding callbacks", e);
        } catch (NoSuchMethodException ignored) {
            return null;
        }

        Class<?> returnType = method.getReturnType();
        if (returnType != void.class && returnType != Symbol.class && returnType != Symbol[].class) {
            throw new IllegalStateException("'" + name + "' returns something different than a Symbol");
        }
        return method;
    }

    private void invokeMethod(Method method, Object[] args) {
        Object ret;
        try {
            method.setAccessible(true);
            ret = method.invoke(this, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not invoke method '" + method.getName() + "'", e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException("Method '" + method.getName() + "' failed", cause);
        }

        if (ret instanceof Symbol) {
            addSymbols((Symbol) ret);
        }
        else if (ret instanceof Symbol[]) {
            addSymbols((Symbol[]) ret);
        }
    }

    /**
     * Inject symbols in the program. Can only be called while clingo is calling an external function of this object.
     *
     * @param symbols the symbols to inject
     */
    public void addSymbols(Symbol... symbols) {
        SymbolSink sink = symbolSink.get();
        if (sink == null) {
            throw new IllegalStateException("symbols can only be injected while grounding");
        }
        long[] symbolLongs = new long[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            symbolLongs[i] = symbols[i].getLong();
        }
        try (Arena arena = Arena.ofConfined()) {
            Clingo.check(clingo_symbol_callback_t.invoke(sink.callback,
                    arena.allocateFrom(ValueLayout.JAVA_LONG, symbolLongs), symbolLongs.length, sink.data));
        }
    }

    /**
     * The sink clingo passes in, which is only valid while it is calling this object.
     */
    private record SymbolSink(MemorySegment callback, MemorySegment data) {
    }
}
