package org.potassco.clingo.symbol;

import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;

public class Supremum extends Symbol {

    protected Supremum(long symbol) {
        super(symbol);
    }

    public Supremum() {
        super(Supremum.create());
    }

    public String getString() {
        return "#sup";
    }

    private static long create() {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_symbol_create_supremum(longByReference);
        return longByReference.getValue();
    }

}
