package org.potassco.args;

public enum Configuration implements Option {
    Auto("auto"),
    Frumpy("frumpy"),
    Jumpy("jumpy"),
    Tweety("tweety"),
    Handy("handy"),
    Crafty("crafty"),
    Trendy("trendy"),
    Many("many");

    private final String mode;

    Configuration(String mode) {
        this.mode = mode;
    }

    @Override
    public String getShellKey() {
        return "--configuration";
    }

    @Override
    public String getNativeKey() {
        return "configuration";
    }

    @Override
    public String getValue() {
        return mode;
    }

    @Override
    public Option getDefault() {
        return Configuration.Auto;
    }
}
