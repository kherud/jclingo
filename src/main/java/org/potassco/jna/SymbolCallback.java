package org.potassco.jna;
import org.potassco.cpp.clingo_h;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback function to inject symbols.
 * 
 * @see ::clingo_ground_callback_t
 * 
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_symbol_callback_t}
 */
public abstract class SymbolCallback implements Callback {
    public abstract boolean call(Pointer symbols, Pointer symbolsSize, Pointer data);

    /**
     * @param symbols array of symbols
     * @param symbolsSize size of the symbol array
     * @param data user data of the callback
     * @return
     */
    public boolean callback(Pointer symbols, Pointer symbolsSize, Pointer data) {
        return call(symbols, symbolsSize, data);
    }
}