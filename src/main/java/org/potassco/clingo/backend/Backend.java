package org.potassco.clingo.backend;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.api.struct.WeightedLiteral;
import org.potassco.clingo.api.types.ExternalType;
import org.potassco.clingo.api.types.HeuristicType;
import org.potassco.clingo.api.types.NativeSize;
import org.potassco.clingo.symbol.Symbol;

/**
 * Backend object providing a low level interface to extend a logic program.
 * This class allows for adding statements in ASPIF format.
 * The `Backend` is a context manager and should be used with Java's `try with` statement.
 * Alternatively it must be closed manually.
 */
public class Backend implements ErrorChecking, AutoCloseable {

    private final Pointer backend;

    public Backend(Pointer backend) {
        this.backend = backend;
        checkError(Clingo.INSTANCE.clingo_backend_begin(backend));
    }

    /**
     * Add an edge directive to the program.
     */
    public void addAcycEdge() {
        // TODO: what are acyc edges?!
//        checkError(Clingo.INSTANCE.clingo_backend_acyc_edge());
    }

    /**
     * Add assumptions to the program.
     */
    // TODO: change int signature
    public void addAssume(int[] literals) {
        checkError(Clingo.INSTANCE.clingo_backend_assume(backend, literals, new NativeSize(literals.length)));
    }

    /**
     * Return a fresh program atom.
     *
     * @return The program atom representing the atom.
     */
    // TODO: change return value
    public int addAtom() {
        IntByReference intByReference = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_backend_add_atom(backend, null, intByReference));
        return intByReference.getValue();
    }

    /**
     * Return a fresh program atom or the atom associated with the given symbol.
     * <p>
     * If the given symbol does not exist in the atom base, it is added first. Such
     * atoms will be used in subequents calls to ground for instantiation.
     *
     * @param symbol The symbol associated with the atom.
     * @return The program atom representing the atom.
     */
    // TODO: change return value
    public int addAtom(Symbol symbol) {
        IntByReference intByReference = new IntByReference();
        LongByReference longByReference = new LongByReference();
        longByReference.setValue(symbol.getLong());
        checkError(Clingo.INSTANCE.clingo_backend_add_atom(backend, longByReference, intByReference));
        return intByReference.getValue();
    }

    /**
     * Mark a program atom as external.
     *
     * @param atom The program atom to mark as external.
     */
    public void addExternal(int atom) {
        checkError(Clingo.INSTANCE.clingo_backend_external(backend, atom, ExternalType.FREE.getValue()));
    }

    /**
     * Mark a program atom as external fixing its truth value.
     * Can also be used to release an external atom using `TruthValue.Release`.
     *
     * @param atom       The program atom to mark as external.
     * @param externalType The truth value.
     */
    public void addExternal(int atom, ExternalType externalType) {
        checkError(Clingo.INSTANCE.clingo_backend_external(backend, atom, externalType.getValue()));
    }

    /**
     * Add a heuristic directive to the program.
     *
     * @param atom          Program atom to heuristically modify.
     * @param condition     List of program literals.
     * @param heuristicType The type of modification.
     * @param bias          A signed integer.
     * @param priority      An unsigned integer.
     */
    public void addHeuristic(int atom, int[] condition, HeuristicType heuristicType, int bias, int priority) {
        checkError(Clingo.INSTANCE.clingo_backend_heuristic(
                backend,
                atom,
                heuristicType.getValue(),
                bias,
                priority,
                condition,
                new NativeSize(condition.length))
        );
    }

    /**
     * Add a minimize constraint to the program.
     *
     * @param literals Array of weighted literals.
     * @param priority Integer for the priority.
     */
    public void addMinimize(WeightedLiteral[] literals, int priority) {
        checkError(Clingo.INSTANCE.clingo_backend_minimize(backend, priority, literals, new NativeSize(literals.length)));
    }

    /**
     * Add a project statement to the program.
     *
     * @param atoms List of program atoms to project on.
     */
    public void addProject(int[] atoms) {
        checkError(Clingo.INSTANCE.clingo_backend_project(backend, atoms, new NativeSize(atoms.length)));
    }

    /**
     * Add a disjuntive or choice rule to the program.
     * <p>
     * Integrity constraints and normal rules can be added by using an empty or singleton head list, respectively.
     *
     * @param head   The program atoms forming the rule head.
     * @param body   The program literals forming the rule body.
     * @param choice Whether to add a disjunctive or choice rule.
     */
    public void addRule(int[] head, int[] body, boolean choice) {
        checkError(Clingo.INSTANCE.clingo_backend_rule(
                backend,
                choice,
                head,
                new NativeSize(head.length),
                body,
                new NativeSize(body.length)
        ));
    }

    /**
     * Add a disjuntive or choice rule with one weight constraint with a lower bound in the body to the program.
     *
     * @param head The program atoms forming the rule head.
     * @param lowerBound The lower bound.
     * @param body The pairs of program literals and weights forming the elements of the weight constraint.
     * @param choice Whether to add a disjunctive or choice rule.
     */
    public void addWeightRule(int[] head, int lowerBound, WeightedLiteral[] body, boolean choice) {
        checkError(Clingo.INSTANCE.clingo_backend_weight_rule(
                backend,
                choice,
                head,
                new NativeSize(head.length),
                lowerBound,
                body,
                new NativeSize(body.length)
        ));
    }

    @Override
    public void close() throws Exception {
        checkError(Clingo.INSTANCE.clingo_backend_end(backend));
    }
}
