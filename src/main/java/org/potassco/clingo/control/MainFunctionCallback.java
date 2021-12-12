package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import org.potassco.clingo.internal.NativeSize;

import javax.security.auth.callback.Callback;

/**
 * Callback to customize clingo main function.
 */
public interface MainFunctionCallback extends Callback {
    /**
     * @param control corresponding control object
     * @param files files passed via command line arguments
     * @param size number of files
     * @param data user data for the callback
     * @return whether the call was successful
     */
    // TODO: is String[] correct here?
    default boolean callback(Pointer control, String[] files, NativeSize size, Pointer data) {
        call(control, files);
        return true;
    }

    void call(Pointer control, String[] files);

}
