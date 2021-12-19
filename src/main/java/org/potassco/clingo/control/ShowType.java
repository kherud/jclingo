package org.potassco.clingo.control;

/**
 * Enumeration of bit flags to select symbols in models.
 * @author Josef Schneeberger
 */
public class ShowType {
    public enum Type {

        CSP(1), // Select CSP assignments.
        SHOWN(2), // Select shown atoms and terms.
        ATOMS(4), // Select all atoms.
        TERMS(8), // Select all atoms.
        THEORY(16), // Select symbols added by theory.
        ALL(31), // Select everything.
        COMPLEMENT(32); // Select false instead of true atoms or terms.

        private final int type;

        Type(int type) {
            this.type = type;
        }
    }

    private int bitset;

    public ShowType(int bitset) {
        this.bitset = bitset;
    }

    public ShowType(ShowType.Type type, ShowType.Type... types) {
        this(type.type);

        for (ShowType.Type addType : types) {
            addType(addType);
        }
    }

    public void addType(ShowType.Type type) {
        this.bitset |= type.type;
    }

    public boolean isType(ShowType.Type type) {
        return (this.bitset & type.type) > 0;
    }

    public static boolean isType(int bitset, ShowType.Type type) {
        return (bitset & type.type) > 0;
    }

    public int getBitset() {
        return bitset;
    }

    public static ShowType shown() {
        return new ShowType(Type.SHOWN);
    }

    public static ShowType atoms() {
        return new ShowType(Type.ATOMS);
    }

    public static ShowType all() {
        return new ShowType(Type.ALL);
    }

}
