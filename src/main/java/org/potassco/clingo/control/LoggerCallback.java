package org.potassco.clingo.control;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback to intercept warning messages.
 */
public abstract class LoggerCallback implements Callback {
    /**
     * @param code associated warning code
     * @param message warning message
     * @param data user data for callback
     */
    public void callback(int code, Pointer message, Pointer data) {
        call(WarningCode.fromValue(code), message, data);
    }

    public abstract boolean call(WarningCode code, Pointer message, Pointer data);

}
