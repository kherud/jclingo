package org.potassco.clingo.symbol;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.api.types.NativeSize;
import org.potassco.clingo.api.types.NativeSizeByReference;

public class Function extends Symbol {

    private final String name;
    private final boolean positive;
    private final Symbol[] arguments;

    protected Function(long symbolId) {
        super(symbolId);

        String[] stringByRef = new String[1];
        ByteByReference byteByRef = new ByteByReference();
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        PointerByReference argumentsReference = new PointerByReference();

        checkError(Clingo.INSTANCE.clingo_symbol_name(symbolId, stringByRef));
        checkError(Clingo.INSTANCE.clingo_symbol_is_positive(symbolId, byteByRef));
        checkError(Clingo.INSTANCE.clingo_symbol_arguments(symbolId, argumentsReference, nativeSizeByReference));

        this.name = stringByRef[0];
        this.positive = byteByRef.getValue() > 0;
        int argCount = (int) nativeSizeByReference.getValue();
        this.arguments = new Symbol[argCount];

        if (argCount > 0) {
            long[] arguments = argumentsReference.getValue().getLongArray(0, argCount);
            for (int i = 0; i < argCount; i++) {
                this.arguments[i] = Symbol.fromLong(arguments[i]);
            }
        }
    }

    public Function(String name, Symbol... arguments) {
        this(name, true, arguments);
    }

    public Function(String name, boolean positive, Symbol... arguments) {
        super(Function.create(name, positive, arguments));
        this.name = name;
        this.positive = positive;
        this.arguments = arguments;
    }

    public String getName() {
        return this.name;
    }

    public boolean isPositive() {
        return this.positive;
    }

    public Symbol[] getArguments() {
        return arguments;
    }

    public int getArity() {
        return arguments.length;
    }

    private static long create(String name, boolean positive, Symbol... arguments) {
        long[] argumentSymbols = new long[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentSymbols[i] = arguments[i].getLong();
        }
        NativeSize argumentsSize = new NativeSize(arguments.length);

        LongByReference longByReference = new LongByReference();
        ErrorChecking.staticCheckError(Clingo.INSTANCE.clingo_symbol_create_function(name, argumentSymbols, argumentsSize, positive, longByReference));
        return longByReference.getValue();
    }
}
