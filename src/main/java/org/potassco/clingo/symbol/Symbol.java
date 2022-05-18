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

import com.sun.jna.Native;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.util.Arrays;

public abstract class Symbol implements Comparable<Symbol> {

    protected final long symbol;

    protected Symbol(long symbol) {
        this.symbol = symbol;
    }

    /**
     * Check if this is a function symbol with the given signature.
     *
     * @param signature the {@link Signature} to match
     * @return Whether the function matches.
     */
    public boolean match(Signature signature) {
        if (!(this instanceof Function))
            return false;
        Function symbol = (Function) this;
        return (symbol.isPositive() == signature.isPositive())
                && (symbol.getName().equals(signature.getName()))
                && (symbol.getArity() == signature.getArity());
    }

    /**
     * Calculate a hash code of a symbol.
     *
     * @return the hash code of the symbol
     */
    public int hash() {
        return Clingo.INSTANCE.clingo_symbol_hash(symbol).intValue();
    }

    /**
     * Get the type of a symbol.
     *
     * @return the {@link SymbolType type} of the symbol
     */
    public SymbolType getType() {
        int typeId = Clingo.INSTANCE.clingo_symbol_type(symbol);
        return SymbolType.fromValue(typeId);
    }

    @Override
    public int compareTo(Symbol other) {
        return equals(other) ? 0 : lessThan(other) ? -1 : 1;
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByRef = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_to_string_size(symbol, nativeSizeByRef));
        int length = (int) nativeSizeByRef.getValue();
        byte[] symbolBytes = new byte[length];
        Clingo.check(Clingo.INSTANCE.clingo_symbol_to_string(symbol, symbolBytes, new NativeSize(length)));
        return Native.toString(symbolBytes);
    }

    /**
     * Parse a term in string form.
     * <p>
     * The result of this function is a symbol. The input term can contain
     * unevaluated functions, which are evaluated during parsing.
     *
     * @param term the string to parse
     */
    public static Symbol fromString(String term) {
        return fromString(term, null);
    }

    /**
     * Parse a term in string form.
     * <p>
     * The result of this function is a symbol. The input term can contain
     * unevaluated functions, which are evaluated during parsing.
     *
     * @param term   the string to parse
     * @param logger optional logger to report warnings during parsing
     */
    public static Symbol fromString(String term, LoggerCallback logger) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_parse_term(term, logger, null, 0, longByReference));
        return Symbol.fromLong(longByReference.getValue());
    }

    /**
     * Returns a java object for the native long symbol
     *
     * @param symbol the native long id
     * @return the java {@link Symbol}
     */
    public static Symbol fromLong(long symbol) {
        int typeId = Clingo.INSTANCE.clingo_symbol_type(symbol);
        SymbolType type = SymbolType.fromValue(typeId);
        switch (type) {
            case INFIMUM:
                return new Infimum(symbol);
            case NUMBER:
                return new Number(symbol);
            case STRING:
                return new Text(symbol);
            case FUNCTION:
                return new Function(symbol);
            case SUPREMUM:
                return new Supremum(symbol);
            default:
                throw new IllegalStateException("unknown symbol type of symbol" + symbol);
        }
    }

    /**
     * Symbol Comparison Functions
     * <p>
     * Check if two symbols are equal.
     *
     * @param other second symbol
     * @return whether this == other
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Symbol))
            return false;
        return Clingo.INSTANCE.clingo_symbol_is_equal_to(symbol, ((Symbol) other).getLong()) > 0;
    }

    /**
     * Check if a symbol is less than another symbol.
     * <p>
     * Symbols are first compared by type. If the types are equal, the values are
     * compared (where strings are compared using strcmp). Functions are first
     * compared by signature and then lexicographically by arguments.
     *
     * @param other second symbol
     * @return whether this < other
     */
    public boolean lessThan(Symbol other) {
        return Clingo.INSTANCE.clingo_symbol_is_less_than(symbol, other.getLong()) > 0;
    }

    public long getLong() {
        return symbol;
    }

}
