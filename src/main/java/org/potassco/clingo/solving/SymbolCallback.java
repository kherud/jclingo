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
