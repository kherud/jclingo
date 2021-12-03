package org.potassco.api.callback;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.potassco.api.struct.Location;

/**
 * Callback function to implement external functions.
 * <p>
 * If an external function of form <tt>\@name(parameters)</tt> occurs in a logic program,
 * then this function is called with its location, name, parameters, and a callback to inject symbols as arguments.
 * The callback can be called multiple times; all symbols passed are injected.
 * <p>
 * If a (non-recoverable) clingo API function fails in this callback, for example, the symbol callback, the callback must return false.
 * In case of errors not related to clingo, this function can set error ::clingo_error_unknown and return false to stop grounding with an error.
 */
public abstract class GroundCallback implements Callback {
    /**
     * @param location location from which the external function was called
     * @param name name of the called external function
     * @param arguments arguments of the called external function
     * @param argumentsSize number of arguments
     * @param data user data of the callback
     * @param symbolCallback function to inject symbols
     * @param symbolCallbackData user data for the symbol callback (must be passed untouched)
     * @return whether the call was successful
     */
    public boolean callback(Pointer location, String name, long[] arguments, long argumentsSize, Pointer data, SymbolCallback symbolCallback, Pointer symbolCallbackData) {
        return call(location, name, arguments, argumentsSize, data, symbolCallback, symbolCallbackData);
    }

    public abstract boolean call(Pointer location, String name, long[] arguments, long argumentsSize, Pointer data, SymbolCallback symbolCallback, Pointer symbolCallbackData);
}
