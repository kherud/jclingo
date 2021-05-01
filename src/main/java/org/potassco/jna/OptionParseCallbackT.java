package org.potassco.jna;

import com.sun.jna.Pointer;

public abstract class OptionParseCallbackT {
    public abstract boolean call(String value, Pointer data);

    public boolean callback(String value, Pointer data) {
        return call(value, data);
    }

}
