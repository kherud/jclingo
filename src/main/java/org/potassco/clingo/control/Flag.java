package org.potassco.clingo.control;

import com.sun.jna.ptr.ByteByReference;

public class Flag {

    private final ByteByReference flag = new ByteByReference();

    public boolean get() {
        return flag.getValue() > 0;
    }

    public void set(boolean value) {
        flag.setValue(value ? (byte) 1 : 0);
    }

    public ByteByReference getFlag() {
        return flag;
    }
}
