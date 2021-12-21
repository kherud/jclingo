package org.potassco.clingo.control;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.solving.Model;

import java.nio.file.Path;

public interface Application {

    /**
     * Run clingo with a this customized main function.
     * Do not ovewrite this function.
     *
     * @param arguments command line arguments
     * @return exit code to return from main function
     */
    default int run(String... arguments) {
        // TODO: maybe set callbacks to null if not implemented
        Clingo.Application nativeApplication = new Clingo.Application();
        nativeApplication.programName = this::getName;
        nativeApplication.version = this::getVersion;
        nativeApplication.messageLimit = this::getMessageLimit;
        nativeApplication.main = this::main;
        nativeApplication.logger = this::log;
        nativeApplication.modelPrinter = this::printModel;
        nativeApplication.registerOptions = this::registerOptions;
        nativeApplication.validateOptions = this::validateOptions;
        return Clingo.INSTANCE.clingo_main(nativeApplication, arguments, new NativeSize(arguments.length), null);
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
     * @return Maximum number of messages defaulting to `20` passed to the logger.
     */
    default int getMessageLimit() {
        // TODO: WHY IS THERE A SEG FAULT WITHOUT THIS PRINT??!
        System.out.println();
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
        System.err.printf("[%s] %s\n", code.getValue(), message);
    }

    /**
     * Function to print additional information when the text output is used.
     *
     * @param model The current model
     */
    default void printModel(Model model) {
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
