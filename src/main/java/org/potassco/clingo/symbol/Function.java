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

import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

/**
 * Construct a function symbol.
 * <p>
 * This includes constants and tuples. Constants have an empty argument list
 * and tuples have an empty name. Functions can represent classically negated
 * atoms. Argument {@link #isPositive()} has to be set to false to represent such atoms.
 */
public class Function extends Symbol {

    /**
     * Creates a java function representation for a native clingo function.
     *
     * @param symbolId the native function id
     */
    protected Function(long symbolId) {
        super(symbolId);
    }

    /**
     * Create a new true Function from a name and a list of arguments.
     *
     * @param name      the name of the Function
     * @param arguments its symbolic arguments
     */
    public Function(String name, Symbol... arguments) {
        this(name, true, arguments);
    }

    /**
     * Create a new true Function from a name and a list of arguments.
     *
     * @param argument  the first element of the Tuple
     * @param arguments more elemenets of the tuple
     */
    public Function(Symbol argument, Symbol... arguments) {
        this(Function.create(argument, arguments));
    }

    /**
     * Create a new Function from a name, its sign, and a list of arguments.
     *
     * @param name      the name of the Function
     * @param positive  whether the function is positive (does not have a sign)
     * @param arguments its symbolic arguments
     */
    public Function(String name, boolean positive, Symbol... arguments) {
        super(Function.create(name, positive, arguments));
    }

    /**
     * @return Get the name of a symbol.
     */
    public String getName() {
        String[] stringByRef = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_symbol_name(symbol, stringByRef));
        return stringByRef[0];
    }

    /**
     * @return Check if a function is positive (does not have a sign).
     */
    public boolean isPositive() {
        ByteByReference byteByRef = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_is_positive(symbol, byteByRef));
        return byteByRef.getValue() > 0;
    }

    /**
     * @return boolean if a function is negative (has a sign).
     */
    public boolean isNegative() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_is_negative(symbol, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @return Get the symbolic arguments of the function.
     */
    public Symbol[] getArguments() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        PointerByReference argumentsReference = new PointerByReference();

        Clingo.check(Clingo.INSTANCE.clingo_symbol_arguments(symbol, argumentsReference, nativeSizeByReference));
        int argCount = (int) nativeSizeByReference.getValue();
        Symbol[] arguments = new Symbol[argCount];

        if (argCount > 0) {
            long[] argumentsLongs = argumentsReference.getValue().getLongArray(0, argCount);
            for (int i = 0; i < argCount; i++) {
                arguments[i] = Symbol.fromLong(argumentsLongs[i]);
            }
        }

        return arguments;
    }

    /**
     * @return The arity of the function.
     */
    public int getArity() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        PointerByReference pointerByReference = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_arguments(symbol, pointerByReference, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }

    /**
     * Statically create a new native id from a name and its sign
     *
     * @param name     the name of the function
     * @param positive the sign of the function
     * @return the newly created native function
     */
    private static long create(String name, boolean positive) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_create_id(name, positive ? (byte) 1 : 0, longByReference));
        return longByReference.getValue();
    }

    /**
     * Statically create a new native tuple from a name and its sign
     *
     * @param symbol  the first symbol of the tuple
     * @param symbols more symbols of the tuple
     * @return the newly created native tuple
     */
    private static long create(Symbol symbol, Symbol[] symbols) {
        long[] arguments = new long[1 + symbols.length];
        arguments[0] = symbol.getLong();
        for (int i = 0; i < symbols.length; i++) {
            arguments[1 + i] = symbols[i].getLong();
        }
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_create_function(
                "",
                arguments,
                new NativeSize(arguments.length),
                (byte) 1,
                longByReference)
        );
        return longByReference.getValue();
    }

    /**
     * Statically create a new native function from a name, its sign, and a list of arguments
     *
     * @param name      the name of the function
     * @param positive  the sign of the function
     * @param arguments the symbol arguments
     * @return the newly created native function
     */
    private static long create(String name, boolean positive, Symbol... arguments) {
        long[] argumentSymbols = new long[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentSymbols[i] = arguments[i].getLong();
        }
        NativeSize argumentsSize = new NativeSize(arguments.length);

        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_create_function(
                name,
                argumentSymbols,
                argumentsSize,
                positive ? (byte) 1 : 0,
                longByReference)
        );
        return longByReference.getValue();
    }
}
