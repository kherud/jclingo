package org.potassco.clingo.symbol;

import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.Clingo;

public class Infimum extends Symbol {

    protected Infimum(long symbol) {
        super(symbol);
    }

    public Infimum() {
        super(Infimum.create());
    }

    public String getString() {
        return "#inf";
    }

    private static long create() {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_symbol_create_infimum(longByReference);
        return longByReference.getValue();
    }

}
