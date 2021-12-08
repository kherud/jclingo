package org.potassco.clingo.symbol;

import com.sun.jna.Native;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.Clingo;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.dtype.NativeSize;
import org.potassco.clingo.dtype.NativeSizeByReference;

public abstract class Symbol implements ErrorChecking, Comparable<Symbol> {

    protected final long symbol;

    protected Symbol(long symbol) {
        this.symbol = symbol;
    }

    public boolean match(String name, int arity, boolean positive) {
        if (!(this instanceof Function))
            return false;
        Function symbol = (Function) this;
        return (symbol.isPositive() == positive) && (symbol.getName().equals(name)) && (symbol.getArity() == arity);
    }

    // TODO: can ints be used here?
    // @Override
    // public int hashCode() {
    //     return Clingo.INSTANCE.clingo_symbol_hash(symbol).intValue();
    // }

    public SymbolType getType() {
        int typeId = Clingo.INSTANCE.clingo_symbol_type(symbol);
        return SymbolType.fromValue(typeId);
    }

    @Override
    public int compareTo(Symbol other){
        return equals(other) ? 0 : lessThan(other) ? -1 : 1;
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByRef = new NativeSizeByReference();
        checkError(Clingo.INSTANCE.clingo_symbol_to_string_size(symbol, nativeSizeByRef));
        int length = (int) nativeSizeByRef.getValue();
        byte[] symbolBytes = new byte[length];
        checkError(Clingo.INSTANCE.clingo_symbol_to_string(symbol, symbolBytes, new NativeSize(length)));
        return Native.toString(symbolBytes);
    }

    public static Symbol fromString(String term) {
        LongByReference longByReference = new LongByReference();
        ErrorChecking.staticCheckError(Clingo.INSTANCE.clingo_parse_term(term, null,  null, 0, longByReference));
        return Symbol.fromLong(longByReference.getValue());
    }

    public static Symbol fromLong(long symbol) {
        int typeId = Clingo.INSTANCE.clingo_symbol_type(symbol);
        SymbolType type = SymbolType.fromValue(typeId);
        switch (type) {
            case INFIMUM: return new Infimum(symbol);
            case NUMBER: return new Number(symbol);
            case STRING: return new Text(symbol);
            case FUNCTION: return new Function(symbol);
            case SUPREMUM: return new Supremum(symbol);
            default:
                throw new IllegalStateException("unknown symbol type of symbol" + symbol);
        }
    }

    public boolean equals(Symbol other) {
        return Clingo.INSTANCE.clingo_symbol_is_equal_to(symbol, other.getLong());
    }

    public boolean lessThan(Symbol other) {
        return Clingo.INSTANCE.clingo_symbol_is_less_than(symbol, other.getLong());
    }

    public long getLong() {
        return symbol;
    }

}
