package org.potassco.clingo.args;

public enum EnumMode implements Option {

    Backtrack("bt"),
    Record("record"),
    Brave("brave"),
    Cautious("cautious"),
    Auto("auto");

    private final String mode;

    EnumMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String getShellKey() {
        return "--enum-mode";
    }

    @Override
    public String getNativeKey() {
        return "solve.enum_mode";
    }

    @Override
    public String getValue() {
        return this.mode;
    }

    @Override
    public Option getDefault() {
        return EnumMode.Auto;
    }
}
