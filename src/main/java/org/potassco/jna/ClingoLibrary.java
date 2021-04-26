package org.potassco.jna;
import org.potassco.cpp.bool;
import org.potassco.cpp.c_char;
import org.potassco.cpp.c_void;
import org.potassco.cpp.clingo_error_t;
import org.potassco.cpp.clingo_h;
import org.potassco.cpp.clingo_signature_t;
import org.potassco.cpp.clingo_warning_t;
import org.potassco.cpp.size_t;
import org.potassco.cpp.uint32_t;

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
    
    /**
     * Obtain the clingo version.
     * <pre>CLINGO_VISIBILITY_DEFAULT void clingo_version(int *major, int *minor, int *revision);</pre>
     * {@link clingo_h#clingo_version(int, int, int)}
     * @param major [out] major major version number
     * @param minor [out] minor minor version number
     * @param patch [out] revision revision number
     */
    void clingo_version(IntByReference major, IntByReference minor, IntByReference patch);

    //! Convert error code into string.
    /** {@link clingo_h#clingo_error_string} */
    public String clingo_error_string(int code);
    //! Get the last error code set by a clingo API call.
    //! @note Each thread has its own local error code.
    //! @return error code
    /** {@link clingo_h#clingo_error_code} */
    public int clingo_error_code();
    //! Get the last error message set if an API call fails.
    //! @note Each thread has its own local error message.
    //! @return error message or NULL
    /** {@link clingo_h#clingo_error_message} */
    public String clingo_error_message();
    //! Set a custom error code and message in the active thread.
    //! @param[in] code the error code
    //! @param[in] message the error message
    /** {@link clingo_h#clingo_set_error} */
    public void clingo_set_error(int code, String message);
    //! Convert warning code into string.
    /** {@link clingo_h#clingo_warning_string} */
    public String clingo_warning_string(int code);

    //! @name Signature Functions
    //! @{

    //! Create a new signature.
    //!
    //! @param[in] name name of the signature
    //! @param[in] arity arity of the signature
    //! @param[in] positive false if the signature has a classical negation sign
    //! @param[out] signature the resulting signature
    //! @return whether the call was successful; might set one of the following error codes:
    //! - ::clingo_error_bad_alloc
    // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_create(char const *name, uint32_t arity, bool positive, clingo_signature_t *signature);
    /** {@link clingo_h#clingo_signature_create} */
    int clingo_signature_create(String p_name, int arity, int positive, PointerByReference p_signature);

    //! Get the name of a signature.
    //!
    //! @note
    //! The string is internalized and valid for the duration of the process.
    //!
    //! @param[in] signature the target signature
    //! @return the name of the signature
    /** {@link clingo_h#clingo_signature_name} */
    public String clingo_signature_name(Pointer signature); // CLINGO_VISIBILITY_DEFAULT char const *clingo_signature_name(clingo_signature_t signature);

    //! Get the arity of a signature.
    //!
    //! @param[in] signature the target signature
    //! @return the arity of the signature
    /** {@link clingo_h#clingo_signature_arity} */
    public int clingo_signature_arity(Pointer signature); // CLINGO_VISIBILITY_DEFAULT uint32_t clingo_signature_arity(clingo_signature_t signature);
  
    //! Whether the signature is positive (is not classically negated).
    //!
    //! @param[in] signature the target signature
    //! @return whether the signature has no sign
    /** {@link clingo_h#clingo_signature_is_positive} */
    public byte clingo_signature_is_positive(Pointer signature); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_positive(clingo_signature_t signature);

    //! Whether the signature is negative (is classically negated).
    //!
    //! @param[in] signature the target signature
    //! @return whether the signature has a sign
    /** {@link clingo_h#clingo_signature_is_negative} */
    public byte clingo_signature_is_negative(Pointer signature); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_negative(clingo_signature_t signature);

    //! Check if two signatures are equal.
    //!
    //! @param[in] a first signature
    //! @param[in] b second signature
    //! @return whether a == b
    /** {@link clingo_h#clingo_signature_is_equal_to} */
    public byte clingo_signature_is_equal_to(Pointer a, Pointer b); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_equal_to(clingo_signature_t a, clingo_signature_t b);
  
    //! Check if a signature is less than another signature.
    //!
    //! Signatures are compared first by sign (unsigned < signed), then by arity,
    //! then by name.
    //!
    //! @param[in] a first signature
    //! @param[in] b second signature
    //! @return whether a < b
    /** {@link clingo_h#clingo_signature_is_less_than} */
    public byte clingo_signature_is_less_than(Pointer a, Pointer b); // CLINGO_VISIBILITY_DEFAULT bool clingo_signature_is_less_than(clingo_signature_t a, clingo_signature_t b);

    //! Calculate a hash code of a signature.
    //!
    //! @param[in] signature the target signature
    //! @return the hash code of the signature
    /** {@link clingo_h#clingo_signature_hash} */
    public Size clingo_signature_hash(Pointer signature); // CLINGO_VISIBILITY_DEFAULT size_t clingo_signature_hash(clingo_signature_t signature);

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
    boolean clingo_control_new(Pointer arguments, Size arguments_size, Pointer logger, Pointer logger_data, int message_limit, PointerByReference control);
    
    /**
     * Free a control object created with clingo_control_new().
     * @param control [in] control the target
     */
 // CLINGO_VISIBILITY_DEFAULT void clingo_control_free(clingo_control_t *control);
    //void clingo_control_free(clingo_control_t *control);
    void clingo_control_free(Pointer control);
    
    //bool clingo_control_add(clingo_control_t *control, char const *name, char const * const * parameters, size_t parameters_size, char const *program);
    boolean clingo_control_add(Pointer control, String name, String[] parameters, Size parameters_size, String program);
    
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
    boolean clingo_control_ground(Pointer control, Part[] parts, Size parts_size, Pointer ground_callback, Pointer ground_callback_data);
        
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
    boolean clingo_control_solve(Pointer control, int mode, Pointer assumptions, Size assumptions_size, SolveEventCallbackT notify, Pointer data, PointerByReference handle);
    
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
   boolean clingo_model_symbols_size(Pointer model, int show, SizeByReference size);
   
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
    boolean clingo_model_symbols(Pointer model, int show, long[] symbols, Size size);
    
    /**
     * Get the size of the string representation of a symbol (including the terminating 0).
     * @param symbol [in] symbol the target symbol
     * @param size [out] size the resulting size
     * @return whether the call was successful; might set one of the following error codes:
     * - ::clingo_error_bad_alloc
     */
//  CLINGO_VISIBILITY_DEFAULT bool clingo_symbol_to_string_size(clingo_symbol_t symbol, size_t *size);
    //bool clingo_symbol_to_string_size(clingo_symbol_t symbol, size_t *size);
    boolean clingo_symbol_to_string_size(long symbol, SizeByReference size);
    
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
    boolean clingo_symbol_to_string(long symbol, byte[] string, Size size);
}