package org.potassco.clingo.control;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import org.potassco.clingo.ast.Location;
import org.potassco.clingo.grounding.SymbolCallback;
import org.potassco.clingo.internal.NativeSize;

import java.util.Arrays;
import java.util.List;

/**
 * Custom scripting language to run functions during grounding.
 */
public class Script extends Structure {

    public interface ExecuteCallback extends Callback {
        /**
         * Evaluate the given source code.
         * @param location the location in the logic program of the source code
         * @param code the code to evaluate
         * @param data user data as given when registering the script
         * @return whether the function call was successful
         */
        boolean callback(Location location, String code, Pointer data);
    }

    // callback to obtain program name
    public interface CallCallback extends Callback {
        /**
         * Call the function with the given name and arguments.
         * @param location the location in the logic program of the function call
         * @param name the name of the function
         * @param arguments the arguments to the function
         * @param argumentsSize the number of arguments
         * @param symbolCallback callback to return a pool of symbols
         * @param symbolCallbackData user data for the symbol callback
         * @param data user data as given when registering the script
         * @return whether the function call was successful
         */
        boolean callback(Location location, String name, long[] arguments, NativeSize argumentsSize, SymbolCallback symbolCallback, Pointer symbolCallbackData, Pointer data);
    }

    // callback to obtain program name
    public interface CallableCallback extends Callback {
        /**
         * Check if the given function is callable.
         * @param name the name of the function
         * @param result whether the function is callable
         * @param data user data as given when registering the script
         * @return whether the function call was successful
         */
        boolean callback(String name, ByteByReference result, Pointer data);
    }

    // callback to obtain program name
    public interface MainCallback extends Callback {
        /**
         * Run the main function.
         * @param control the control object to pass to the main function
         * @param data user data as given when registering the script
         * @return whether the function call was successful
         */
        boolean callback(Pointer control, Pointer data);
    }

    // callback to obtain program name
    public interface FreeCallback extends Callback {
        /**
         * This function is called once when the script is deleted.
         * @param data user data as given when registering the script
         */
        String callback(Pointer data);
    }

    public ExecuteCallback execute;
    public CallCallback call;
    public CallableCallback callable;
    public MainCallback main;
    public FreeCallback free;
    public String version;

    protected List<String> getFieldOrder() {
        return Arrays.asList("execute", "call", "callable", "main", "free", "version");
    }
}
