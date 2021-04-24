package org.potassco.jna;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface ClingoLibrary extends Library {
    ClingoLibrary INSTANCE = Native.load("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll", ClingoLibrary.class);

    interface SolveEventCallback extends ClingoLibrary {
        boolean callback(int type, Pointer event, Pointer data, Pointer goon);
    }
    
//    void printf(String format, Object... args);
    /**
     * Obtain the clingo version.
     * <pre>CLINGO_VISIBILITY_DEFAULT void clingo_version(int *major, int *minor, int *revision);</pre>
     * @param major [out] major major version number
     * @param minor [out] minor minor version number
     * @param patch [out] revision revision number
     */
    void clingo_version(IntByReference major, IntByReference minor, IntByReference patch);

  //! @return whether the call was successful; might set one of the following error codes:
  //! - ::clingo_error_bad_alloc
  //! - ::clingo_error_runtime if argument parsing fails
//  CLINGO_VISIBILITY_DEFAULT bool clingo_control_new(char const *const * arguments, size_t arguments_size, clingo_logger_t logger, void *logger_data, unsigned message_limit, clingo_control_t **control);
    //bool clingo_control_new(char const *const * arguments, size_t arguments_size, clingo_logger_t logger, void *logger_data, int message_limit, clingo_control_t **control);
    /**
     * Create a new control object.
     * <p>
     * A control object has to be freed using clingo_control_free().
     * <p>
     * @note Only gringo options (without <code>\-\-output</code>) and clasp's options are supported as arguments,
     * except basic options such as <code>\-\-help</code>.
     * Furthermore, a control object is blocked while a search call is active;
     * you must not call any member function during search.
     * <p>
     * If the logger is NULL, messages are printed to stderr.
     * @param arguments [in] arguments C string array of command line arguments
     * @param arguments_size [in] arguments_size size of the arguments array
     * @param logger [in] logger callback functions for warnings and info messages
     * @param logger_data [in] logger_data user data for the logger callback
     * @param message_limit [in] message_limit maximum number of times the logger callback is called
     * @param control [out] control resulting control object
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - ::clingo_error_runtime if argument parsing fails
     */
    boolean clingo_control_new(Pointer arguments, SizeT arguments_size, Pointer logger, Pointer logger_data, int message_limit, PointerByReference control);
    
    /**
     * Free a control object created with clingo_control_new().
     * @param control [in] control the target
     */
 // CLINGO_VISIBILITY_DEFAULT void clingo_control_free(clingo_control_t *control);
    //void clingo_control_free(clingo_control_t *control);
    void clingo_control_free(Pointer control);
    
    //bool clingo_control_add(clingo_control_t *control, char const *name, char const * const * parameters, size_t parameters_size, char const *program);
    boolean clingo_control_add(Pointer control, String name, String[] parameters, SizeT parameters_size, String program);
    
    /**Ground the selected @link ::clingo_part parts @endlink of the current (non-ground) logic program.
     * <p>
     * After grounding, logic programs can be solved with ::clingo_control_solve().
     * <p>
     * @note Parts of a logic program without an explicit <tt>\#program</tt>
     * specification are by default put into a program called `base` without
     * arguments.
     * @param control [in] control the target
     * @param parts [in] parts array of parts to ground
     * @param parts_size [in] parts_size size of the parts array
     * @param ground_callback [in] ground_callback callback to implement external functions
     * @param ground_callback_data [in] ground_callback_data user data for ground_callback
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - error code of ground callback
     * @see clingo_part
     * bool clingo_control_ground(clingo_control_t *control, clingo_part_t const *parts, size_t parts_size, clingo_ground_callback_t ground_callback, void *ground_callback_data);
     *  CLINGO_VISIBILITY_DEFAULT bool clingo_control_ground(clingo_control_t *control, clingo_part_t const *parts, size_t parts_size, clingo_ground_callback_t ground_callback, void *ground_callback_data);
     */
    boolean clingo_control_ground(Pointer control, PartT[] parts, SizeT parts_size, Pointer ground_callback, Pointer ground_callback_data);
        
    /**
     * Solve the currently @link ::clingo_control_ground grounded @endlink logic program enumerating its models.
     * <p>
     * See the @ref SolveHandle module for more information.
     * @param control [in] control the target
     * @param mode [in] mode configures the search mode
     * @param assumptions [in] assumptions array of assumptions to solve under
     * @param assumptions_size [in] assumptions_size number of assumptions
     * @param notify [in] notify the event handler to register
     * @param data [in] data the user data for the event handler
     * @param handle [out] handle handle to the current search to enumerate models
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - ::clingo_error_runtime if solving could not be started
     */
//  CLINGO_VISIBILITY_DEFAULT bool clingo_control_solve(clingo_control_t *control, clingo_solve_mode_bitset_t mode, clingo_literal_t const *assumptions, size_t assumptions_size, clingo_solve_event_callback_t notify, void *data, clingo_solve_handle_t **handle);
    //bool clingo_control_solve(clingo_control_t *control, clingo_solve_mode_bitset_t mode, clingo_literal_t const *assumptions, size_t assumptions_size, clingo_solve_event_callback_t notify, void *data, clingo_solve_handle_t **handle);
    boolean clingo_control_solve(Pointer control, int mode, Pointer assumptions, SizeT assumptions_size, SolveEventCallbackT notify, Pointer data, PointerByReference handle);
    
    /**
     * Get the next solve result.
     * <p>
     * Blocks until the result is ready.
     * When yielding partial solve results can be obtained, i.e.,
     * when a model is ready, the result will be satisfiable but neither the search exhausted nor the optimality proven.
     * @param handle [in] handle the target
     * @param result [out] result the solve result
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - ::clingo_error_runtime if solving fails
     */
//  CLINGO_VISIBILITY_DEFAULT bool clingo_solve_handle_get(clingo_solve_handle_t *handle, clingo_solve_result_bitset_t *result);
//bool clingo_solve_handle_get(clingo_solve_handle_t *handle, clingo_solve_result_bitset_t *result);
    boolean clingo_solve_handle_get(Pointer handle, IntByReference result);
    
    /**
     * Stops the running search and releases the handle.
     * <p>
     * Blocks until the search is stopped (as if an implicit cancel was called before the handle is released).
     * @param handle [in] handle the target
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - ::clingo_error_runtime if solving fails
     */
    // CLINGO_VISIBILITY_DEFAULT bool clingo_solve_handle_close(clingo_solve_handle_t *handle);
    //bool clingo_solve_handle_close(clingo_solve_handle_t *handle);
    boolean clingo_solve_handle_close(Pointer handle);
    
    /**
     * Get the number of symbols of the selected types in the model.
     * @param model [in] model the target
     * @param show [in] show which symbols to select
     * @param size [out] size the number symbols
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     */
//  CLINGO_VISIBILITY_DEFAULT bool clingo_model_symbols_size(clingo_model_t const *model, clingo_show_type_bitset_t show, size_t *size);
    //bool clingo_model_symbols_size(clingo_model_t const *model, clingo_show_type_bitset_t show, size_t *size);
   boolean clingo_model_symbols_size(Pointer model, int show, SizeTByReference size);
   
    /**
     * Get the symbols of the selected types in the model.
     * <p>
     * @note CSP assignments are represented using functions with name "$"
     * where the first argument is the name of the CSP variable and the second one its
     * value.
     * @param model [in] model the target
     * @param show [in] show which symbols to select
     * @param symbols [out] symbols the resulting symbols
     * @param size [in] size the number of selected symbols
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * - ::clingo_error_runtime if the size is too small
     * @see clingo_model_symbols_size()
     */
// CLINGO_VISIBILITY_DEFAULT bool clingo_model_symbols(clingo_model_t const *model, clingo_show_type_bitset_t show, clingo_symbol_t *symbols, size_t size);
   //bool clingo_model_symbols(clingo_model_t const *model, clingo_show_type_bitset_t show, clingo_symbol_t *symbols, size_t size);
    boolean clingo_model_symbols(Pointer model, int show, long[] symbols, SizeT size);
    
    /**
     * Get the size of the string representation of a symbol (including the terminating 0).
     * @param symbol [in] symbol the target symbol
     * @param size [out] size the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     */
//  CLINGO_VISIBILITY_DEFAULT bool clingo_symbol_to_string_size(clingo_symbol_t symbol, size_t *size);
    //bool clingo_symbol_to_string_size(clingo_symbol_t symbol, size_t *size);
    boolean clingo_symbol_to_string_size(long symbol, SizeTByReference size);
    
    /**
     * Get the string representation of a symbol.
     * @param symbol [in] symbol the target symbol
     * @param string [out] string the resulting string
     * @param size [in] size the size of the string
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     * @see clingo_symbol_to_string_size()
     */
//  CLINGO_VISIBILITY_DEFAULT bool clingo_symbol_to_string(clingo_symbol_t symbol, char *string, size_t size);
    //bool clingo_symbol_to_string(clingo_symbol_t symbol, char *string, size_t size);
    boolean clingo_symbol_to_string(long symbol, byte[] string, SizeT size);
}