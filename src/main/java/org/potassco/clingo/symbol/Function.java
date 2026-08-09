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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

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
        return Out.getString(out -> ClingoH.clingo_symbol_name(symbol, out));
    }

    /**
     * @return Check if a function is positive (does not have a sign).
     */
    public boolean isPositive() {
        return Out.getBoolean(out -> ClingoH.clingo_symbol_is_positive(symbol, out));
    }

    /**
     * @return boolean if a function is negative (has a sign).
     */
    public boolean isNegative() {
        return Out.getBoolean(out -> ClingoH.clingo_symbol_is_negative(symbol, out));
    }

    /**
     * @return Get the symbolic arguments of the function.
     */
    public Symbol[] getArguments() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment argumentsOut = arena.allocate(ValueLayout.ADDRESS);
            MemorySegment sizeOut = arena.allocate(ValueLayout.JAVA_LONG);
            Clingo.check(ClingoH.clingo_symbol_arguments(symbol, argumentsOut, sizeOut));
            int argumentCount = (int) sizeOut.get(ValueLayout.JAVA_LONG, 0);
            long[] argumentSymbols = argumentsOut.get(ValueLayout.ADDRESS, 0)
                    .reinterpret(argumentCount * Long.BYTES)
                    .toArray(ValueLayout.JAVA_LONG);
            Symbol[] arguments = new Symbol[argumentCount];
            for (int i = 0; i < argumentCount; i++) {
                arguments[i] = Symbol.fromLong(argumentSymbols[i]);
            }
            return arguments;
        }
    }

    /**
     * @return The arity of the function.
     */
    public int getArity() {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sizeOut = arena.allocate(ValueLayout.JAVA_LONG);
            Clingo.check(ClingoH.clingo_symbol_arguments(symbol, arena.allocate(ValueLayout.ADDRESS), sizeOut));
            return (int) sizeOut.get(ValueLayout.JAVA_LONG, 0);
        }
    }

    /**
     * Statically create a new native id from a name and its sign
     *
     * @param name     the name of the function
     * @param positive the sign of the function
     * @return the newly created native function
     */
    private static long create(String name, boolean positive) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            return Out.getLong(out -> ClingoH.clingo_symbol_create_id(name$, positive, out));
        }
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
        return create("", true, arguments);
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
        return create(name, positive, argumentSymbols);
    }

    private static long create(String name, boolean positive, long[] arguments) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            MemorySegment arguments$ = arena.allocateFrom(ValueLayout.JAVA_LONG, arguments);
            return Out.getLong(out ->
                    ClingoH.clingo_symbol_create_function(name$, arguments$, arguments.length, positive, out));
        }
    }
}
