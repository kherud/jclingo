package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;
import org.potassco.clingo.symbol.Signature;
import org.potassco.clingo.symbol.Symbol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class provides read-only access to the atom base of the grounder.
 */
public class SymbolicAtoms implements Iterable<SymbolicAtom> {

    private final Pointer symbolicAtoms;

    public SymbolicAtoms(Pointer symbolicAtoms) {
        this.symbolicAtoms = symbolicAtoms;
    }

    /**
     * @param symbol the symbol to return the symbolic atom for
     * @return the symbolic atom of the symbol
     */
    public SymbolicAtom getSymbolicAtom(Symbol symbol) {
        LongByReference longByReference = new LongByReference();
        ByteByReference byteByReference = new ByteByReference();

        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_find(symbolicAtoms, symbol.getLong(), longByReference));
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_is_valid(symbolicAtoms, longByReference.getValue(), byteByReference));

        if (byteByReference.getValue() == 0)
            throw new NoSuchElementException("Symbol '" + symbol + "' has no symbolic atom");

        return new SymbolicAtom(symbolicAtoms, longByReference.getValue());
    }

    /**
     * @return all symbolic atoms in the program
     */
    public List<SymbolicAtom> getAll() {
        List<SymbolicAtom> symbolicAtoms = new ArrayList<>();
        iterator().forEachRemaining(symbolicAtoms::add);
        return symbolicAtoms;
    }

    /**
     * @param symbol the symbol to check for if a symbolic atom exists
     * @return a boolean indicating whether the symbolic atom exists
     */
    public boolean contains(Symbol symbol) {
        LongByReference longByReference = new LongByReference();
        ByteByReference byteByReference = new ByteByReference();

        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_find(symbolicAtoms, symbol.getLong(), longByReference));
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_is_valid(symbolicAtoms, longByReference.getValue(), byteByReference));

        return byteByReference.getValue() > 0;
    }

    /**
     * @return The amount of symbolic atoms in the program.
     */
    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_size(symbolicAtoms, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }

    /**
     * @return The number of different predicate signatures used in the program.
     */
    public int amountSignatures() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_signatures_size(symbolicAtoms, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }

    /**
     * @return The list of predicate signatures occurring in the program.
     */
    public List<Signature> getSignatures() {
        int amountSignatures = amountSignatures();
        long[] signaturesLongs = new long[amountSignatures];
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_signatures(symbolicAtoms, signaturesLongs, new NativeSize(amountSignatures)));
        List<Signature> signatures = new ArrayList<>();
        for (long signature : signaturesLongs) {
            signatures.add(new Signature(signature));
        }
        return signatures;
    }

    /**
     * @return an iterator over all symbolic atoms in the program
     */
    @Override
    public Iterator<SymbolicAtom> iterator() {
        return iterator(nativeIterator());
    }

    /**
     * @return an iterator over all symbolic atoms in the program matching the signature
     */
    public Iterator<SymbolicAtom> iterator(Signature signature) {
        long nativeIterator = nativeIteratorBySignature(signature.getLong());
        return iterator(nativeIterator);
    }

    private long nativeIteratorBySignature(long signature) {
        LongByReference iteratorReference = new LongByReference();
        LongByReference signatureReference = new LongByReference(signature);
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_begin(symbolicAtoms, signatureReference, iteratorReference));
        return iteratorReference.getValue();
    }

    private long nativeIterator() {
        LongByReference iteratorReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_begin(symbolicAtoms, null, iteratorReference));
        return iteratorReference.getValue();
    }

    private boolean nativeIteratorEquals(long iteratorA, long iteratorB) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_iterator_is_equal_to(symbolicAtoms, iteratorA, iteratorB, byteByReference));
        return byteByReference.getValue() > 0;
    }

    private Iterator<SymbolicAtom> iterator(long nativeIterator) {
        return new Iterator<>() {

            // allocate these again instead of calling class methods to save allocations
            private final LongByReference longByReference = new LongByReference(nativeIterator);
            private final ByteByReference byteByReference = new ByteByReference();

            @Override
            public boolean hasNext() {
                Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_is_valid(symbolicAtoms, longByReference.getValue(), byteByReference));
                return byteByReference.getValue() > 0;
            }

            @Override
            public SymbolicAtom next() {
                SymbolicAtom symbolicAtom = new SymbolicAtom(symbolicAtoms, longByReference.getValue());
                Clingo.check(Clingo.INSTANCE.clingo_symbolic_atoms_next(symbolicAtoms, longByReference.getValue(), longByReference));
                return symbolicAtom;
            }
        };
    }
}
