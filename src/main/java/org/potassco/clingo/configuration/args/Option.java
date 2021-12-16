package org.potassco.clingo.configuration.args;

public interface Option {
    String getShellKey();
    String getNativeKey();
    String getValue();
    String toString();
    Option getDefault();
}
