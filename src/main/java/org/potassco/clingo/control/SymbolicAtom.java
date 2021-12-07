package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.symbol.Symbol;

public class SymbolicAtom implements ErrorChecking {

    private Symbol symbol;

    public SymbolicAtom(Pointer pointer) {

    }

    public Symbol getSymbol() {
        if (symbol != null)
            return symbol;
//        Clingo.INSTANCE.clingo_symbolic_atoms_symbol()
        return null;
    }

    public boolean match(String name, int arity, boolean positive) {
        return getSymbol().match(name, arity, positive);
    }
}
