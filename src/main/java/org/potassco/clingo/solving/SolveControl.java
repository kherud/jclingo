package org.potassco.clingo.solving;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.api.types.TruthValue;
import org.potassco.clingo.control.SymbolicAtom;
import org.potassco.clingo.control.SymbolicAtoms;

import java.util.Collection;

/**
 * Object that allows for controlling a running search.
 */
// TODO: implement
public class SolveControl implements ErrorChecking {

    private final Pointer solveControl;

    public SolveControl(Pointer solveControl) {
        this.solveControl = solveControl;
    }

    /**
     * Add a clause that applies to the current solving step during the search.
     *
     * This function can only be called in a model callback or while iterating when using a `SolveHandle`.
     *
     * @param symbols List of symbolic atoms.
     * @param truthValue the truth value to assign the atoms to.
     */
    public void addClause(Collection<SymbolicAtom> symbols, TruthValue truthValue) {

    }

    /**
     * @return object to inspect the symbolic atoms.
     */
    public SymbolicAtoms getSymbolicAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_solve_control_symbolic_atoms(solveControl, pointerByReference));
        return new SymbolicAtoms(pointerByReference.getValue());
    }
}
