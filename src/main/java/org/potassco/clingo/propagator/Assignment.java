package org.potassco.clingo.propagator;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import org.potassco.clingo.Clingo;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.dtype.NativeSize;
import org.potassco.clingo.TruthValue;

import java.util.Iterator;

/**
 * Class to inspect the (parital) assignment of an associated solver.
 *
 * Assigns truth values to solver literals.  Each solver literal is either
 * true, false, or undefined.
 *
 * This class implements `Iterable[Integer]` to access the (positive)
 * literals in the assignment.
 */
public class Assignment implements Iterable<Integer>, ErrorChecking {

    private final Pointer assignment;

    public Assignment(Pointer assignment) {
        this.assignment = assignment;
    }

    /**
     * @return The number of (positive) literals in the assignment.
     */
    public int size() {
        return Clingo.INSTANCE.clingo_assignment_size(assignment).intValue();
    }

    /**
     * @param index the index of the literal
     * @return The (positive) literal at the given offset in the assignment.
     */
    public int getAssignmentAt(int index) {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_assignment_at(assignment, new NativeSize(index), intByReference));
        return intByReference.getValue();
    }

    /**
     * @return the decision literal of the given level.
     */
    public int getDecision(int level) {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_assignment_decision(assignment, level, intByReference));
        return intByReference.getValue();
    }

    /**
     * @param literal The solver literal.
     * @return a bool determining if the given literal is valid in this solver.
     */
    public boolean hasLiteral(int literal) {
        return Clingo.INSTANCE.clingo_assignment_has_literal(assignment, literal);
    }

    /**
     * @param literal The solver literal.
     * @return a bool determining if the literal is false.
     */
    public boolean isFalse(int literal) {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_assignment_is_false(assignment, literal, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @param literal The solver literal.
     * @return a bool determining if the literal is assigned on the top level.
     */
    public boolean isFixed(int literal) {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_assignment_is_fixed(assignment, literal, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @param literal The solver literal.
     * @return a bool determining if the literal is true.
     */
    public boolean isTrue(int literal) {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_assignment_is_true(assignment, literal, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * @param literal The solver literal.
     * @return a bool determining if the literal is assigned on the top level.
     */
    public boolean isFree(int literal) {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_assignment_truth_value(assignment, literal, intByReference));
        return intByReference.getValue() == TruthValue.FREE.getValue();
    }

    public int getLevel(int literal) {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_assignment_level(assignment, literal, intByReference));
        return intByReference.getValue();
    }



    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<>() {

            private final int size = size();
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < size - 1;
            }

            @Override
            public Integer next() {
                return getAssignmentAt(i++);
            }
        };
    }
}
