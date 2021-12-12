package org.potassco.clingo.control;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback to intercept warning messages.
 */
public interface LoggerCallback extends Callback {
    /**
     * @param code associated warning code
     * @param message warning message
     * @param data user data for callback
     */
    default void callback(int code, String message, Pointer data) {
        call(WarningCode.fromValue(code), message);
    }

    void call(WarningCode code, String message);

}
