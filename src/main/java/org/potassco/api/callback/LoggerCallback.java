package org.potassco.api.callback;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.potassco.api.enums.WarningCode;

/**
 * Callback to intercept warning messages.
 */
public abstract class LoggerCallback implements Callback {
    /**
     * @param code associated warning code
     * @param message warning message
     * @param data user data for callback
     */
    public void callback(WarningCode code, Pointer message, Pointer data) {
        call(code, message, data);
    }

    public abstract boolean call(WarningCode code, Pointer message, Pointer data);

}
