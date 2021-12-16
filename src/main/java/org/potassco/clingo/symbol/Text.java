package org.potassco.clingo.symbol;

import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;

public class Text extends Symbol {

    private final String text;

    protected Text(long symbol) {
        super(symbol);
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_symbol_string(symbol, stringByReference));
        this.text = stringByReference[0];
    }

    public Text(String text) {
        super(Text.create(text));
        this.text = text;
    }

    public String getText() {
        return text;
    }

    private static long create(String text) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbol_create_string(text, longByReference));
        return longByReference.getValue();
    }
}
