package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.api.callback.LoggerCallback;
import org.potassco.clingo.api.types.NativeSizeByReference;
import org.potassco.clingo.symbol.Symbol;

import java.util.Iterator;

/**
 * This class holds a reference to the native symbolic atoms of a clingo program
 */
public class SymbolicAtoms implements Iterable<SymbolicAtom>, ErrorChecking {

    private final Pointer symbolicAtoms;
    private final LoggerCallback logger;
    private final int messageLimit;

    // allocate these only once for performance
    private final LongByReference longByRef = new LongByReference();
    private final IntByReference intByRef = new IntByReference();
    private final ByteByReference byteByRef = new ByteByReference();
    private final NativeSizeByReference nativeSizeByRef = new NativeSizeByReference();

    public SymbolicAtoms(Pointer symbolicAtoms) {
        this(symbolicAtoms, null, 0);
    }

    public SymbolicAtoms(Pointer symbolicAtoms, LoggerCallback logger, int messageLimit) {
        this.symbolicAtoms = symbolicAtoms;
        this.logger = logger;
        this.messageLimit = messageLimit;
    }

    /**
     * This method parses a string to clingo's native literal id
     * @param term the string to parse
     * @return the long id of a valid literal
     */
    public int stringToLiteral(String term) {
        long symbol = stringToSymbol(term);
        long iterator = iteratorFind(symbol);

        if (!isValid(iterator))
            throw new IllegalStateException("symbolic atom '" + term + "' not found");

        return iteratorToLiteral(iterator);
    }

    public long stringToSymbol(String term) {
        // parse a string term to a clingo symbol id
        checkError(Clingo.INSTANCE.clingo_parse_term(term, logger, null, 1000, longByRef));

        return longByRef.getValue();
    }

    public int symbolToLiteral(Symbol symbol) {
        return symbolToLiteral(symbol.getLong());
    }

    public int symbolToLiteral(long symbol) {
        long iterator = iteratorFind(symbol);
        return iteratorToLiteral(iterator);
    }

    public int iteratorToLiteral(long iterator) {
        // finally, get the corresponding literal id to the symbol
        checkError(Clingo.INSTANCE.clingo_symbolic_atoms_literal(symbolicAtoms, iterator, intByRef));
        return intByRef.getValue();
    }

    public long size() {
        Clingo.INSTANCE.clingo_symbolic_atoms_size(symbolicAtoms, nativeSizeByRef);
        return nativeSizeByRef.getValue();
    }

    private boolean isValid(long iterator) {
        // using the iterator, check if the symbol is valid in the context of the program
        checkError(Clingo.INSTANCE.clingo_symbolic_atoms_is_valid(symbolicAtoms, iterator, byteByRef));
        return byteByRef.getValue() == 1;
    }

    private long iteratorFind(long symbol) {
        // get an iterator reference to the symbol in the programs symbol set
        checkError(Clingo.INSTANCE.clingo_symbolic_atoms_find(symbolicAtoms, symbol, longByRef));
        return longByRef.getValue();
    }

    @Override
    public Iterator<SymbolicAtom> iterator() {
        return null;
    }
}
