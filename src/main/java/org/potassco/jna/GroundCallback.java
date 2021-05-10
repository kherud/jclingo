package org.potassco.jna;

import org.potassco.cpp.clingo_h;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * 
 *  Callback function to implement external functions.
 * 
 *  If an external function of form <tt>\@name(parameters)</tt> occurs in a logic program,
 *  then this function is called with its location, name, parameters, and a callback to inject symbols as arguments.
 *  The callback can be called multiple times; all symbols passed are injected.
 * 
 *  If a (non-recoverable) clingo API function fails in this callback, for example, the symbol callback, the callback must return false.
 *  In case of errors not related to clingo, this function can set error ::clingo_error_unknown and return false to stop grounding with an error.
 * 
 *  @param[in] location location from which the external function was called
 *  @param[in] name name of the called external function
 *  @param[in] arguments arguments of the called external function
 *  @param[in] arguments_size number of arguments
 *  @param[in] data user data of the callback
 *  @param[in] symbol_callback function to inject symbols
 *  @param[in] symbol_callback_data user data for the symbol callback
 *             (must be passed untouched)
 *  @return whether the call was successful
 *  @see clingo_control_ground()
 * 
 *  The following example implements the external function <tt>\@f()</tt> returning 42.
 *  ~~~~~~~~~~~~~~~{.c}
 *  bool
 *  ground_callback(clingo_location_t const *location,
 *                  char const *name,
 *                  clingo_symbol_t const *arguments,
 *                  size_t arguments_size,
 *                  void *data,
 *                  clingo_symbol_callback_t symbol_callback,
 *                  void *symbol_callback_data) {
 *    if (strcmp(name, "f") == 0 && arguments_size == 0) {
 *      clingo_symbol_t sym;
 *      clingo_symbol_create_number(42, &sym);
 *      return symbol_callback(&sym, 1, symbol_callback_data);
 *    }
 *    clingo_set_error(clingo_error_runtime, "function not found");
 *    return false;
 *  }
 *  ~~~~~~~~~~~~~~~
 * @author Josef Schneeberger
 * {@link clingo_h#clingo_ground_callback_t}
 */
public abstract class GroundCallback implements Callback {
    public abstract boolean call(Pointer location, String name, Pointer arguments, long argumentsSize, Pointer data, SymbolCallback symbolCallback, Pointer symbolCallbackData);

    /**
     * @param symbols array of symbols
     * @param symbolsSize size of the symbol array
     * @param data user data of the callback
     * @return
     */
 // typedef bool (*clingo_ground_callback_t) (clingo_location_t const *location, char const *name, clingo_symbol_t const *arguments, size_t arguments_size, void *data, clingo_symbol_callback_t symbol_callback, void *symbol_callback_data);
    public boolean callback(Pointer location, String name, Pointer arguments, long argumentsSize, Pointer data, SymbolCallback symbolCallback, Pointer symbolCallbackData) {
        return call(location, name, arguments, argumentsSize, data, symbolCallback, symbolCallbackData);
    }
}