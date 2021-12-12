package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.ErrorChecking;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.Symbol;

/**
 * Captures a symbolic atom and provides properties to inspect its state.
 */
public class SymbolicAtom implements ErrorChecking {

    private final Pointer symbolicAtoms;
    private final long iterator;

    /**
     * Creates a symbolic atom object from a pointer to a native symbolic atom set
     * and an associated iterator pointing to the specific atom.
     *
     * You probably do not want to call this explicitly, rather use the functionality of {@link SymbolicAtoms}
     *
     * @param symbolicAtoms pointer to the native symbolic atom set
     * @param iterator native iterator pointing to the atom
     */
    public SymbolicAtom(Pointer symbolicAtoms, long iterator) {
        this.symbolicAtoms = symbolicAtoms;
        this.iterator = iterator;
    }

    /**
     * @return Whether the atom is an external atom.
     */
    public boolean isExternal() {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_symbolic_atoms_is_external(symbolicAtoms, iterator, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @return Whether the atom is a fact.
     */
    public boolean isFact() {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_symbolic_atoms_is_fact(symbolicAtoms, iterator, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @return The program literal associated with the atom.
     */
    public int getLiteral() {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_symbolic_atoms_literal(symbolicAtoms, iterator, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return The representation of the atom in form of a symbol.
     */
    public Symbol getSymbol() {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_symbolic_atoms_symbol(symbolicAtoms, iterator, longByReference);
        return Symbol.fromLong(longByReference.getValue());
    }

    /**
     * @param signature name, arity, and positivity of the function.
     * @return Whether the atom matches the signature.
     */
    public boolean match(Signature signature) {
        return getSymbol().match(signature);
    }
}
