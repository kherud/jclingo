/*
 * Copyright (C) 2021 denkbares GmbH. All rights reserved.
 */

package org.potassco.clingo.configuration.args;

public enum Heuristic implements Option {
    Berkmin("Berkmin", false),
    Vmtf("Vmtf", true),
    Vsids("Vsids", true),
    Domain("Domain", false),
    Unit("Unit", false),
    None("None", false);

    private final String mode;
    private final boolean requiresFactor;
    private String factor;

    Heuristic(String mode, boolean requiresFactor) {
        this.mode = mode;
        this.requiresFactor = requiresFactor;
    }

    public void setFactor(int factor) {
        this.factor = String.valueOf(factor);
    }

    @Override
    public String getShellKey() {
        return "--heuristic";
    }

    @Override
    public String getNativeKey() {
        return "solver.heuristic";
    }

    @Override
    public String getValue() {
        if (requiresFactor && factor == null)
            throw new IllegalStateException("Heuristic '" + mode + "' requires a factor");
        return mode;
    }

    @Override
    public Option getDefault() {
        Heuristic heuristic = Heuristic.Vsids;
        heuristic.setFactor(92);
        return heuristic;
    }
}
