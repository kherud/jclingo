package org.potassco.jna;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface CLibrary extends Library {
    CLibrary INSTANCE = Native.load("d:\\js\\projects\\clingo4j\\windows\\x64\\clingo.dll", CLibrary.class);

    void printf(String format, Object... args);
    void clingo_version(IntByReference major, IntByReference minor, IntByReference patch);
    //bool clingo_control_new(char const *const * arguments, size_t arguments_size, clingo_logger_t logger, void *logger_data, int message_limit, clingo_control_t **control);
    boolean clingo_control_new(Pointer arguments, SizeT arguments_size, Pointer logger, Pointer logger_data, int message_limit, PointerByReference control);
    //void clingo_control_free(clingo_control_t *control);
    void clingo_control_free(Pointer control);
    //bool clingo_control_add(clingo_control_t *control, char const *name, char const * const * parameters, size_t parameters_size, char const *program);
    boolean clingo_control_add(Pointer control, String name, String[] parameters, SizeT parameters_size, String program);
    //bool clingo_control_ground(clingo_control_t *control, clingo_part_t const *parts, size_t parts_size, clingo_ground_callback_t ground_callback, void *ground_callback_data);
    boolean clingo_control_ground(Pointer control, PartT[] parts, SizeT parts_size, Pointer ground_callback, Pointer ground_callback_data);
    //bool clingo_control_solve(clingo_control_t *control, clingo_solve_mode_bitset_t mode, clingo_literal_t const *assumptions, size_t assumptions_size, clingo_solve_event_callback_t notify, void *data, clingo_solve_handle_t **handle);
    boolean clingo_control_solve(Pointer control, int mode, Pointer assumptions, SizeT assumptions_size, SolveEventCallbackT notify, Pointer data, PointerByReference handle);
    //bool clingo_solve_handle_get(clingo_solve_handle_t *handle, clingo_solve_result_bitset_t *result);
    boolean clingo_solve_handle_get(Pointer handle, IntByReference result);
    //bool clingo_solve_handle_close(clingo_solve_handle_t *handle);
    boolean clingo_solve_handle_close(Pointer handle);
    //bool clingo_model_symbols_size(clingo_model_t const *model, clingo_show_type_bitset_t show, size_t *size);
    boolean clingo_model_symbols_size(Pointer model, int show, SizeTByReference size);
    //bool clingo_model_symbols(clingo_model_t const *model, clingo_show_type_bitset_t show, clingo_symbol_t *symbols, size_t size);
    boolean clingo_model_symbols(Pointer model, int show, long[] symbols, SizeT size);
    //bool clingo_symbol_to_string_size(clingo_symbol_t symbol, size_t *size);
    boolean clingo_symbol_to_string_size(long symbol, SizeTByReference size);
    //bool clingo_symbol_to_string(clingo_symbol_t symbol, char *string, size_t size);
    boolean clingo_symbol_to_string(long symbol, byte[] string, SizeT size);
}