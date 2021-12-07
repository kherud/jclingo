package org.potassco.clingo.theory;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.api.types.NativeSizeByReference;

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
