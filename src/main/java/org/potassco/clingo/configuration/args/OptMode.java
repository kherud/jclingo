package org.potassco.clingo.configuration.args;

public enum OptMode implements Option {

    Optimal("opt", false),
    Enum("enum", true),
    OptimalN("optN", false),
    Ignore("ignore", false);

    private final String mode;
    private final boolean requiresBound;
    private String bound;

    OptMode(String mode, boolean requiresBound) {
        this.mode = mode;
        this.requiresBound = requiresBound;
    }

    public void setBound(String bound) {
        this.bound = bound;
    }

    @Override
    public String getShellKey() {
        return "--opt-mode";
    }

    @Override
    public String getNativeKey() {
        return "solve.opt_mode";
    }

    @Override
    public String getValue() {
        if (requiresBound && bound == null)
            throw new IllegalStateException("OptMode '" + mode + "' requires a bound");
        return this.mode;
    }

    @Override
    public Option getDefault() {
        return OptMode.Optimal;
    }

}
