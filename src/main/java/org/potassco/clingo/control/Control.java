package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.Clingo;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.grounding.Observer;
import org.potassco.clingo.grounding.GroundCallback;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.dtype.NativeSize;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.TruthValue;
import org.potassco.clingo.backend.Backend;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.symbol.Symbol;
import org.potassco.clingo.theory.TheoryAtoms;

import java.nio.file.Path;
import java.util.Collection;

public class Control implements ErrorChecking {
    private final Pointer control;
    private final Configuration configuration;
    private final Statistics statistics;
    private final LoggerCallback logger;

    public Control(String program) {
        this(program, null, 0);
    }

    public Control(String program, LoggerCallback logger) {
        this(program, logger, 20);
    }

    public Control(String program, LoggerCallback logger, int messageLimit) {
        this.logger = logger;
        PointerByReference controlObjectRef = new PointerByReference();
        // create control object, do not set any arguments, since we want to set them when calling `solve`
        checkError(Clingo.INSTANCE.clingo_control_new(null, null, logger, null, messageLimit, controlObjectRef));
        this.control = controlObjectRef.getValue();

        // add program
//        this.add("base", program);

        // ground it, we only have a single program (so far)
        ProgramPart programPart = new ProgramPart("base");
        this.ground(programPart);

        this.configuration = new Configuration(this);
        this.statistics = new Statistics(this);
    }

    /**
     * Extend the logic program with the given non-ground logic program in string form.
     *
     * @param name       The name of program block to add.
     * @param parameters The parameters of the program block to add.
     * @param program    The non-ground program in string form.
     */
    public void add(String name, String[] parameters, String program) {
        checkError(Clingo.INSTANCE.clingo_control_add(
                control,
                name,
                parameters, new NativeSize(parameters.length),
                program));
    }

    /**
     * Ground the given list of program parts specified by tuples of names and arguments.
     * <p>
     * Note that parts of a logic program without an explicit `#program`
     * specification are by default put into a program called `base` without arguments.
     *
     * @param programParts Objects of program names and program arguments to ground.
     */
    public void ground(ProgramPart... programParts) {
        checkError(Clingo.INSTANCE.clingo_control_ground(
                this.control,
                programParts, new NativeSize(programParts.length),
                null, null)
        );
    }

    // TODO: implement ground callback
    public void ground(GroundCallback groundCallback, ProgramPart... programParts) {
        checkError(Clingo.INSTANCE.clingo_control_ground(
                this.control,
                programParts, new NativeSize(programParts.length),
                groundCallback, null)
        );
    }

    /**
     * Starts a search.
     *
     * @param assumptions Collection of symbols
     * @param solveMode whether the search is blocking / non-blocking
     * @return a solve-handle to interact with
     */
    public SolveHandle solve(Collection<Symbol> assumptions, SolveMode solveMode) {
        SymbolicAtoms symbolicAtoms = getSymbolicAtoms();
        int[] assumptionLiterals = assumptions.stream().mapToInt(symbolicAtoms::symbolToLiteral).toArray();
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_solve(
                control,
                solveMode.getValue(),
                assumptionLiterals,
                new NativeSize(assumptionLiterals.length),
                null,
                null,
                pointerByReference
        ));
        return new SolveHandle(pointerByReference.getValue());
    }

    /**
     * Extend the logic program with a (non-ground) logic program in a file.
     *
     * @param path The path of the file to load.
     */
    public void load(Path path) {
        checkError(Clingo.INSTANCE.clingo_control_load(control, path.toString()));
    }

    /**
     * Assign a truth value to an external atom.
     * <p>
     * The truth value of an external atom can be changed before each solve
     * call. An atom is treated as external if it has been declared using an
     * `#external` directive, and has not been released by calling
     * `Control.release_external` or defined in a logic program with some
     * rule. If the given atom is not external, then the function has no
     * effect.
     * <p>
     * For convenience, the truth assigned to atoms over negative program
     * literals is inverted.
     *
     * @param external   A symbol representing the external atom.
     * @param truthValue Fixes the external to the respective truth value.
     */
    public void assignExternal(SymbolicAtom external, TruthValue truthValue) {
//        checkError(Clingo.INSTANCE.clingo_control_assign_external(
//                control,
//                external.getLiteral(),
//                truthValue.getValue())
//        );
    }

    /**
     * Assigns truth values to multiple external literals of the program
     *
     * @param externals  Multiple symbols representing external atoms.
     * @param truthValue Fixes the external to the respective truth value.
     */
    public void assignExternal(Collection<SymbolicAtom> externals, TruthValue truthValue) {
        for (SymbolicAtom external : externals) {
            assignExternal(external, truthValue);
        }
    }

    /**
     * Release an external atom represented by the given symbol or program literal.
     * <p>
     * This function causes the corresponding atom to become permanently false
     * if there is no definition for the atom in the program. Otherwise, the
     * function has no effect.
     *
     * @param external The symbolic atom or program atom to release.
     */
    public void releaseExternal(SymbolicAtom external) {
//        checkError(Clingo.INSTANCE.clingo_control_release_external(control, external.getLiteral()));
    }

    /**
     * Releases the truth values of multiple external symbolic atoms of the program
     *
     * @param externals Mulitple symbols representing external atoms.
     */
    public void releaseExternal(Collection<SymbolicAtom> externals) {
        for (SymbolicAtom external : externals) {
            releaseExternal(external);
        }
    }

    /**
     * Interrupt the active solve call.
     * <p>
     * This function is thread-safe and can be called from a signal handler. If no
     * search is active, the subsequent call to `Control.solve` is interrupted. The
     * result of the `Control.solve` method can be used to query if the search was
     * interrupted.
     */
    public void interrupt() {
        Clingo.INSTANCE.clingo_control_interrupt(control);
    }


    /**
     * Cleanup the domain used for grounding by incorporating information from the solver.
     * <p>
     * This function cleans up the domain used for grounding. This is done by
     * first simplifying the current program representation (falsifying
     * released external atoms).  Afterwards, the top-level implications are
     * used to either remove atoms from the domain or mark them as facts.
     * <p>
     * Any atoms falsified are completely removed from the logic program.
     * Hence, a definition for such an atom in a successive step introduces a
     * fresh atom.
     * <p>
     * With the current implementation, the function only has an effect if
     * called after solving and before any function is called that starts a
     * new step.
     * <p>
     * Typically, it is not necessary to call this function manually because
     * automatic cleanups are enabled by default.
     */
    public void cleanup() {
        checkError(Clingo.INSTANCE.clingo_control_cleanup(control));
    }

    /**
     * @return Check whether automatic cleanup is enabled.
     */
    public boolean getCleanupEnabled() {
        return Clingo.INSTANCE.clingo_control_get_enable_cleanup(control);
    }

    /**
     * Enable automatic cleanup after solving. Cleanup is enabled by default.
     *
     * @param value boolean whether to enable or disable cleanup
     */
    public void setEnableCleanup(boolean value) {
        checkError(Clingo.INSTANCE.clingo_control_set_enable_cleanup(control, value));
    }

    /**
     * Whether do discard or keep learnt information from enumeration modes.
     * <p>
     * If the enumeration assumption is enabled, then all information learnt from
     * clasp's various enumeration modes is removed after a solve call. This includes
     * enumeration of cautious or brave consequences, enumeration of answer sets with
     * or without projection, or finding optimal models; as well as clauses added with
     * `clingo.solving.SolveControl.add_clause`.
     * <p>
     * Initially the enumeration assumption is enabled.
     * <p>
     * In general, the enumeration assumption should be enabled whenever there are
     * multiple calls to solve. Otherwise, the behavior of the solver will be
     * unpredictable because there are no guarantees which information exactly is
     * kept. There might be small speed benefits when disabling the enumeration
     * assumption for single shot solving.
     *
     * @return whether the enumeration assumption is enabled
     */
    public boolean getEnumerationAssumptionEnabled() {
        return Clingo.INSTANCE.clingo_control_get_enable_enumeration_assumption(control);
    }

    /**
     * Whether do discard or keep learnt information from enumeration modes.
     * <p>
     * If the enumeration assumption is enabled, then all information learnt from
     * clasp's various enumeration modes is removed after a solve call. This includes
     * enumeration of cautious or brave consequences, enumeration of answer sets with
     * or without projection, or finding optimal models; as well as clauses added with
     * `clingo.solving.SolveControl.add_clause`.
     * <p>
     * Initially the enumeration assumption is enabled.
     * <p>
     * In general, the enumeration assumption should be enabled whenever there are
     * multiple calls to solve. Otherwise, the behavior of the solver will be
     * unpredictable because there are no guarantees which information exactly is
     * kept. There might be small speed benefits when disabling the enumeration
     * assumption for single shot solving.
     *
     * @param value a boolean whether to enable or disable the enumeration assumption
     */
    public void setEnableEnumerationAssumption(boolean value) {
        checkError(Clingo.INSTANCE.clingo_control_set_enable_enumeration_assumption(control, value));
    }

    /**
     * Registers the given observer to inspect the produced grounding.
     *
     * @param observer The observer to register. See below for a description of the required interface.
     */
    public void registerObserver(Observer observer) {
        registerObserver(observer, false);
    }

    /**
     * Registers the given observer to inspect the produced grounding.
     *
     * @param observer The observer to register. See below for a description of the required interface.
     * @param replace  If set to true, the output is just passed to the observer and no longer to
     *                 the underlying solver (or any previously registered observers).
     */
    public void registerObserver(Observer observer, boolean replace) {
        // TODO: fix callback data
        checkError(Clingo.INSTANCE.clingo_control_register_observer(control, observer, replace, null));
    }

    /**
     * Registers the given propagator with all solvers.
     * <p>
     * If the sequential flag is set to true, the propagator is called sequentially
     * when solving with multiple threads (default should be false).
     *
     * @param propagator   The propagator to register.
     * @param sequentially Whether to call the propagator sequentially
     */
    public void registerPropagator(Propagator propagator, boolean sequentially) {
        checkError(Clingo.INSTANCE.clingo_control_register_propagator(control, propagator, null, sequentially));
    }

    /**
     * Whether the internal program representation is conflicting.
     * <p>
     * If this result is true, solve calls return immediately with an
     * unsatisfiable solve result.
     * <p>
     * Conflicts first have to be detected, e.g., initial unit propagation results in
     * an empty clause, or later if an empty clause is resolved during solving. Hence,
     * the property might be false even if the problem is unsatisfiable.
     *
     * @return Whether the internal program representation is conflicting.
     */
    public boolean isConflicting() {
        return Clingo.INSTANCE.clingo_control_is_conflicting(control);
    }

    /**
     * Return the symbol for a constant definition of form `#const name = symbol`.
     *
     * @param name The name of the constant to retrieve.
     * @return The constant
     */
    public Symbol getConstant(String name) {
        ByteByReference byteByReference = new ByteByReference();
        checkError(Clingo.INSTANCE.clingo_control_has_const(control, name, byteByReference));

        if (byteByReference.getValue() == 0) {
            throw new IllegalStateException("Constant '" + name + "' not found");
        }

        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_control_get_const(control, name, longByReference);

        return Symbol.fromLong(longByReference.getValue());
    }

    public Pointer getPointer() {
        return control;
    }

    /**
     * @return a `Backend` object providing a low level interface to extend a logic program.
     */
    public Backend getBackend() {
        PointerByReference backendRef = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_backend(control, backendRef));
        return new Backend(backendRef.getValue());
    }

    public SymbolicAtoms getSymbolicAtoms() {
        PointerByReference symbolicAtomsRef = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_symbolic_atoms(control, symbolicAtomsRef));
        return new SymbolicAtoms(symbolicAtomsRef.getValue());
    }

    public TheoryAtoms getTheoryAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_theory_atoms(control, pointerByReference));
        return new TheoryAtoms(pointerByReference.getValue());
    }

    /**
     * @return Object to change the configuration.
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    public Statistics getStatistics() {
        return this.statistics;
    }

    /**
     * Frees the native control object
     */
    public void close() {
        Clingo.INSTANCE.clingo_control_free(control);
    }
}
