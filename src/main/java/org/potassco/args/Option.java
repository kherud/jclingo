package org.potassco.args;

public interface Option {
    String getShellKey();
    String getNativeKey();
    String getValue();
    String toString();
    Option getDefault();
}
