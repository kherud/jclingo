package org.potassco.api.callback;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

// parse callback to parse the value of the option
public abstract class ParseCallback implements Callback {
    /**
     * @param value the value of the option
     * @param data callback data
     * @return whether the call was successful
     */
    public boolean callback(String value, Pointer data) {
        return call(value, data);
    }

    public abstract boolean call(String value, Pointer data);

}
