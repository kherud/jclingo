package org.potassco.clingo.internal;

public interface JnaEnum<T> {
    int getIntValue();
    T getForValue(int i);
}
