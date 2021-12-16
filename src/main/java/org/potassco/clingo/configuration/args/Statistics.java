/*
 * Copyright (C) 2021 denkbares GmbH. All rights reserved.
 */

package org.potassco.clingo.configuration.args;

public enum Statistics implements Option {

    noStatistics("0"),
    basicStatistics("1"),
    fullStatistics("2");

    private final String level;

    Statistics(String level) {
        this.level = level;
    }

    @Override
    public String getShellKey() {
        return "--stats";
    }

    @Override
    public String getNativeKey() {
        return "stats";
    }

    @Override
    public String getValue() {
        return this.level;
    }

    @Override
    public Option getDefault() {
        return Statistics.noStatistics;
    }
}
