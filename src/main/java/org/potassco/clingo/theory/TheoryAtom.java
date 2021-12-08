package org.potassco.clingo.theory;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.Clingo;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.dtype.NativeSize;
import org.potassco.clingo.dtype.NativeSizeByReference;

/**
 * Class to represent theory atoms.
 *
 * Theory atoms have a readable string representation, implement Python's rich
 * comparison operators, and can be used as dictionary keys.
 */
public class TheoryAtom implements Comparable<TheoryAtom>, ErrorChecking {

    private final Pointer theoryAtoms;
    private final int id;

    // TODO: abstract basic structure?
    public TheoryAtom(Pointer theoryAtoms, int id) {
        this.theoryAtoms = theoryAtoms;
        this.id = id;
    }

    public boolean equals(TheoryAtom other) {
        return this.hashCode() == other.hashCode();
    }

    public int compareTo(TheoryAtom other) {
        return Integer.compare(this.hashCode(), other.hashCode());
    }

    @Override
    public int hashCode() {
        return id;
    }

    /**
     * @return The program literal associated with the atom
     */
    public int getLiteral() {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_theory_atoms_atom_literal(theoryAtoms, id, intByReference));
        return intByReference.getValue();
    }

    /**
     * @return The guard of the atom or null if the atom has no guard
     */
    public TheoryGuard getGuard() {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_theory_atoms_atom_has_guard(theoryAtoms, id, byteByReference));

        if (byteByReference.getValue() == 0)
            return null;

        String[] stringByReference = new String[1];
        IntByReference intByReference = new IntByReference();

        checkError(Clingo.INSTANCE.clingo_theory_atoms_atom_guard(theoryAtoms, id, stringByReference, intByReference));
        TheoryTerm theoryTerm = new TheoryTerm(theoryAtoms, intByReference.getValue());
        return new TheoryGuard(stringByReference[0], theoryTerm);
    }

    /**
     * @return The term of the atom
     */
    public TheoryTerm getTerm() {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_theory_atoms_atom_term(theoryAtoms, id, intByReference));
        return new TheoryTerm(theoryAtoms, intByReference.getValue());
    }

    /**
     * @return The elements of the atom
     */
    public TheoryElement[] getElements() {
        PointerByReference pointerByReference = new PointerByReference();
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        checkError(Clingo.INSTANCE.clingo_theory_atoms_atom_elements(theoryAtoms, id, pointerByReference, nativeSizeByReference));
        int amountElements = (int) nativeSizeByReference.getValue();
        TheoryElement[] theoryElements = new TheoryElement[amountElements];

        if (amountElements > 0) {
            int[] theoryElementInts = pointerByReference.getValue().getIntArray(0, amountElements);
            for (int i = 0; i < amountElements; i++) {
                theoryElements[i] = new TheoryElement(theoryAtoms, theoryElementInts[i]);
            }
        }
        return theoryElements;
    }

    @Override
    public String toString() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        checkError(Clingo.INSTANCE.clingo_theory_atoms_atom_to_string_size(theoryAtoms, id, nativeSizeByReference));
        int stringSize = (int) nativeSizeByReference.getValue();
        byte[] atomBytes = new byte[stringSize];
        checkError(Clingo.INSTANCE.clingo_theory_atoms_atom_to_string(theoryAtoms, id, atomBytes, new NativeSize(stringSize)));
        return Native.toString(atomBytes);
    }
}
