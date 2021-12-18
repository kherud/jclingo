package org.potassco.clingo.symbol;

import com.sun.jna.Native;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.nio.charset.StandardCharsets;

/**
 * Construct a function symbol.
 * <p>
 * This includes constants and tuples. Constants have an empty argument list
 * and tuples have an empty name. Functions can represent classically negated
 * atoms. Argument `positive` has to be set to false to represent such atoms.
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

//    public String toString() {
//        String argumentsString = arguments.length == 0 ? "" : "(" + Arrays.stream(arguments).map(Symbol::toString).collect(Collectors.joining(",")) + ")";
//        return (positive ? "" : "-") + name + argumentsString;
//    }

    /**
     * Statically create a new native function from a name and its sign
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
