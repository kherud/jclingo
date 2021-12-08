package org.potassco.clingo.grounding;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.potassco.clingo.dtype.NativeSize;

/**
 *  Callback function to inject symbols.
 */
public abstract class SymbolCallback implements Callback {
    /**
     * @param symbols array of symbols
     * @param symbolsSize size of the symbol array
     * @param data user data of the callback
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     */
    public boolean callback(long[] symbols, NativeSize symbolsSize, Pointer data) {
        return call(symbols, symbolsSize, data);
    }

    public abstract boolean call(long[] symbols, NativeSize symbolsSize, Pointer data);
}
