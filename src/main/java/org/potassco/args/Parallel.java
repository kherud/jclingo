package org.potassco.args;

public class Parallel implements Option {

    private final int nThreads;
    private final Mode mode;

    public Parallel(int nThreads) {
        this(nThreads, Mode.Split);
    }

    public Parallel(int nThreads, Mode mode) {
        if (nThreads < 1 || nThreads > 64)
            throw new IllegalStateException("Amount of threads n used by clingo must be 0 < n <= 64");
        this.nThreads = nThreads;
        this.mode = mode;
    }

    public enum Mode {
        Compete("compete"),
        Split("split");

        private final String mode;

        Mode(String mode) {
            this.mode = mode;
        }

        @Override
        public String toString() {
            return mode;
        }
    }

    public static Parallel single() {
        return new Parallel(1);
    }

    public static Parallel two() {
        return new Parallel(2);
    }

    public static Parallel available() {
        int nCores = Runtime.getRuntime().availableProcessors();
        return new Parallel(nCores);
    }

    @Override
    public String getShellKey() {
        return "--parallel-mode";
    }

    @Override
    public String getNativeKey() {
        return "solve.parallel_mode";
    }

    @Override
    public String getValue() {
        return String.format("%d,%s", nThreads, mode.toString());
    }

    @Override
    public Option getDefault() {
        return Parallel.single();
    }
}
