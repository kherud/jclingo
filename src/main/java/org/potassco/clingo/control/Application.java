package org.potassco.clingo.control;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.file.Path;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.solving.Model;

public interface Application {

    /**
     * Run clingo with this customized main function.
     * Do not overwrite this function.
     *
     * @param arguments command line arguments
     * @return exit code to return from main function
     */
    default int run(String... arguments) {
        Clingo.init();
        // TODO: maybe set callbacks to null if not implemented
        // clingo keeps the callbacks, the parse callbacks of the options and the flags they are bound to until the
        // run is over, and calls some of them from solver threads, so they all live in one shared arena
        try (Arena arena = Arena.ofShared()) {
            MemorySegment application = ApplicationBridge.of(this, arena);
            MemorySegment arguments$ = arena.allocate(ValueLayout.ADDRESS, arguments.length);
            for (int i = 0; i < arguments.length; i++) {
                arguments$.setAtIndex(ValueLayout.ADDRESS, i, arena.allocateFrom(arguments[i]));
            }
            return ClingoH.clingo_main(application, arguments$, arguments.length, MemorySegment.NULL);
        }
    }

    /**
     * @return Program name defaulting to 'clingo' used in the help output.
     */
    default String getName() {
        return "clingo";
    }

    /**
     * @return Version string defaulting to clingo's version.
     */
    default String getVersion() {
        return Clingo.getVersion();
    }

    /**
     * @return Maximum number of messages defaulting to <code>20</code> passed to the logger.
     */
    default int getMessageLimit() {
        return 20;
    }

    /**
     * Function to replace clingo's default main function.
     *
     * @param control   The main control object.
     * @param filePaths The files passed to clingo_main.
     */
    default void main(Control control, Path[] filePaths) {

    }

    /**
     * Function to intercept messages normally printed to standard error.
     * By default, messages are printed to standard error.
     * This function should not raise exceptions.
     *
     * @param code    The message code.
     * @param message The message string.
     */
    default void log(WarningCode code, String message) {
        //noinspection UseOfSystemOutOrSystemErr
        System.err.printf("[%s] %s\n", code.getValue(), message);
    }

    /**
     * Function to print additional information when the text output is used.
     *
     * @param model The current model
     */
    default void printModel(Model model) {
        //noinspection UseOfSystemOutOrSystemErr
        System.out.println(model);
    }

    /**
     * Function to register custom options.
     *
     * @param options Object to register additional options
     */
    default void registerOptions(ApplicationOptions options) {

    }

    /**
     * Function to validate custom options.
     *
     * @return This function should return false if option validation fails.
     */
    default boolean validateOptions() {
        return true;
    }
}
