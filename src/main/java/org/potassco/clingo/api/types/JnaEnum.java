package org.potassco.clingo.api.types;

public interface JnaEnum<T> {
    int getIntValue();
    T getForValue(int i);
}
