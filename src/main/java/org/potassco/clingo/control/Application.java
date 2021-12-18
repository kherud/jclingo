/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
 
package org.potassco.clingo.control;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.solving.Model;
import org.potassco.clingo.solving.ModelPrinterCallback;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * This struct contains a set of functions to customize the clingo application.
 * To implement your own application you have to inherit from this class and
 * overwrite its public functions (except {@link Application#run}!).
 */
public class Application extends Structure {

    /**
     * Run clingo with a this customized main function.
     * Do not ovewrite this function.
     *
     * @param arguments command line arguments
     * @return exit code to return from main function
     */
    public int run(String... arguments) {
        return Clingo.INSTANCE.clingo_main(this, arguments, new NativeSize(arguments.length), null);
    }

    /**
     * @return Program name defaulting to 'clingo' used in the help output.
     */
    public String getName() {
        return "clingo";
    }

    /**
     * @return Version string defaulting to clingo's version.
     */
    public String getVersion() {
        return Clingo.getVersion();
    }

    /**
     * @return Maximum number of messages defaulting to `20` passed to the logger.
     */
    public int getMessageLimit() {
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
    public void main(Control control, Path[] filePaths) {

    }

    /**
     * Function to intercept messages normally printed to standard error.
     * By default, messages are printed to standard error.
     * This function should not raise exceptions.
     *
     * @param code    The message code.
     * @param message The message string.
     */
    public void log(WarningCode code, String message) {
        System.err.printf("[%s] %s\n", code.getValue(), message);
    }

    /**
     * Function to print additional information when the text output is used.
     *
     * @param model The current model
     */
    public void printModel(Model model) {
        System.out.println(model);
    }

    /**
     * Function to register custom options.
     *
     * @param options Object to register additional options
     */
    public void registerOptions(ApplicationOptions options) {

    }

    /**
     * Function to validate custom options.
     *
     * @return This function should return false if option validation fails.
     */
    public boolean validateOptions() {
        return true;
    }


    /**
     * JNA DECLARATIONS IGNORE
     */

    public ProgramNameCallback programName = this::getName; // callback to obtain program name
    public VersionCallback version = this::getVersion; // callback to obtain version information
    public MessageLimitCallback messageLimit = this::getMessageLimit; // callback to obtain message limit
    public MainFunctionCallback main = this::main; // callback to override clingo's main function
    public LoggerCallback logger = this::log; // callback to override default logger
    public ModelPrinterCallback modelPrinter = this::printModel; // callback to override default model printing
    public RegisterOptionsCallback registerOptions = this::registerOptions; // callback to register options
    public ValidateOptionsCallback validateOptions = this::validateOptions; // callback validate options

    // callback to obtain program name
    private interface ProgramNameCallback extends Callback {
        default String callback(Pointer data) {
            return call();
        }

        String call();
    }

    // callback to obtain version information
    private interface VersionCallback extends Callback {
        default String callback(Pointer data) {
            return call();
        }

        String call();
    }

    // callback to obtain message limit
    private interface MessageLimitCallback extends Callback {
        default int callback(Pointer data) {
            return call();
        }

        int call();
    }

    private interface MainFunctionCallback extends Callback {
        default boolean callback(Pointer control, String[] files, NativeSize size, Pointer data) {
            int amountFiles = size.intValue();
            Path[] filePaths = new Path[amountFiles];
            for (int i = 0; i < amountFiles; i++) {
                filePaths[i] = Paths.get(files[i]);
            }
            call(new Control(control), filePaths);
            return true;
        }

        void call(Control control, Path[] filePaths);

    }

    // callback to register options
    private interface RegisterOptionsCallback extends Callback {
        default boolean callback(Pointer options, Pointer data) {
            call(new ApplicationOptions(options));
            return true;
        }

        void call(ApplicationOptions options);
    }

    // callback validate options
    private interface ValidateOptionsCallback extends Callback {
        default boolean callback(Pointer data) {
            return validate();
        }

        boolean validate();
    }

    protected List<String> getFieldOrder() {
        return Arrays.asList("programName", "version", "messageLimit", "main", "logger", "modelPrinter", "registerOptions", "validateOptions");
    }

}
