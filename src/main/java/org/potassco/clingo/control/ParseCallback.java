package org.potassco.clingo.control;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

// parse callback to parse the value of the option
public interface ParseCallback extends Callback {
    /**
     * @param value the value of the option
     * @param data callback data
     * @return whether the call was successful
     */
    default boolean callback(String value, Pointer data) {
        call(value);
        return true;
    }

    /**
     * parse callback to parse the value of the option
     * @param value the value of the option
     */
    void call(String value);

}
