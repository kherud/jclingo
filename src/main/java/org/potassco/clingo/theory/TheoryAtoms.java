package org.potassco.clingo.theory;

import com.sun.jna.Pointer;
import org.potassco.clingo.Clingo;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.internal.NativeSizeByReference;

import java.util.Iterator;

public class TheoryAtoms implements Iterable<TheoryAtom>, ErrorChecking {

    private final Pointer theoryAtoms;

    public TheoryAtoms(Pointer theoryAtoms) {
        this.theoryAtoms = theoryAtoms;
    }

    @Override
    public Iterator<TheoryAtom> iterator() {
        return new Iterator<>() {

            private final int size = size();
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public TheoryAtom next() {
                return new TheoryAtom(theoryAtoms, i++);
            }
        };
    }

    public TheoryAtom getTheoryAtom(int index) {
        return new TheoryAtom(theoryAtoms, index);
    }

    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.INSTANCE.clingo_theory_atoms_size(theoryAtoms, nativeSizeByReference);
        return (int) nativeSizeByReference.getValue();
    }
}
