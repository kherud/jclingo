package org.potassco.clingo.symbol;

import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;

public class Number extends Symbol {

    private final int number;

    protected Number(long symbol) {
        super(symbol);
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_number(symbol, intByReference));
        this.number = intByReference.getValue();
    }

    public Number(int number) {
        super(Number.create(number));
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    private static long create(int number) {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_symbol_create_number(number, longByReference);
        return longByReference.getValue();
    }
}
