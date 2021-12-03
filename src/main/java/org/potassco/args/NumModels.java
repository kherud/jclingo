package org.potassco.args;

public class NumModels implements Option {

    private final int amountModels;

    public NumModels(int amountModels) {
        this.amountModels = amountModels;
    }

    public static NumModels all() {
        return new NumModels(0);
    }

    public static NumModels one() {
        return new NumModels(1);
    }

    public static NumModels optimal() {
        return new NumModels(-1);
    }

    @Override
    public String getShellKey() {
        return "--models";
    }

    @Override
    public String getNativeKey() {
        return "solve.models";
    }

    @Override
    public String getValue() {
        return String.valueOf(amountModels);
    }

    @Override
    public Option getDefault() {
        return NumModels.optimal();
    }
}
