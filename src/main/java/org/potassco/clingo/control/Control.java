package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.ErrorChecking;
import org.potassco.clingo.grounding.Observer;
import org.potassco.clingo.grounding.GroundCallback;
import org.potassco.clingo.propagator.Propagator;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.solving.SolveEventCallback;
import org.potassco.clingo.solving.SolveMode;
import org.potassco.clingo.solving.TruthValue;
import org.potassco.clingo.backend.Backend;
import org.potassco.clingo.solving.SolveHandle;
import org.potassco.clingo.statistics.Statistics;
import org.potassco.clingo.symbol.Symbol;
import org.potassco.clingo.theory.TheoryAtoms;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

/**
 * Control object for the grounding/solving process.
 * <p>
 * Note that only gringo options (without `--text`) and clasp's search options
 * are supported. Furthermore, you must not call any functions of a `Control`
 * object while a solve call is active.
 */
public class Control implements ErrorChecking, AutoCloseable {
    private final Pointer control;
    private final LoggerCallback logger;
    private final int messageLimit;

    public Control() {
        this(null, 0);
    }

    /**
     * @param arguments Arguments to the grounder and solver like in the CLI, e.g. {"--models", "0"}
     */
    public Control(String... arguments) {
        this(null, 0, arguments);
    }

    /**
     * @param arguments    Arguments to the grounder and solver like in the CLI, e.g. {"--models", "0"}
     * @param logger       Callback to intercept messages normally printed to standard error.
     * @param messageLimit The maximum number of messages passed to the logger.
     */
    public Control(LoggerCallback logger, int messageLimit, String... arguments) {
        PointerByReference controlObjectRef = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_new(
                arguments,
                new NativeSize(arguments == null ? 0 : arguments.length),
                logger,
                null,
                messageLimit,
                controlObjectRef)
        );
        this.control = controlObjectRef.getValue();
        this.logger = logger;
        this.messageLimit = messageLimit;
    }

    /**
     * You probably do not want to call this constructor.
     * @param pointer pointer to a native control object.
     */
    public Control(Pointer pointer) {
        this.control = pointer;
        this.logger = null;
        this.messageLimit = 0;
    }

    /**
     * Extend the logic program with the given non-ground logic program in string form.
     *
     * @param program The non-ground program in string form.
     */
    public void add(String program) {
        add("base", program, new String[0]);
    }

    /**
     * Extend the logic program with the given non-ground logic program in string form.
     *
     * @param name    The name of program block to add.
     * @param program The non-ground program in string form.
     */
    public void add(String name, String program) {
        add(name, program, new String[0]);
    }

    /**
     * Extend the logic program with the given non-ground logic program in string form.
     *
     * @param name       The name of program block to add.
     * @param parameters The parameters of the program block to add.
     * @param program    The non-ground program in string form.
     */
    public void add(String name, String program, String... parameters) {
        checkError(Clingo.INSTANCE.clingo_control_add(
                control,
                name,
                parameters, new NativeSize(parameters.length),
                program));
    }

    /**
     * Ground the base program.
     */
    public void ground() {
        ProgramPart[] programParts = (ProgramPart[]) new ProgramPart("base").toArray(1);
        checkError(Clingo.INSTANCE.clingo_control_ground(
                this.control,
                programParts, new NativeSize(1),
                null, null)
        );
    }

    /**
     * Ground the given list of program parts specified by tuples of names and arguments.
     * <p>
     * Note that parts of a logic program without an explicit `#program`
     * specification are by default put into a program called `base` without arguments.
     *
     * @param programParts Objects of program names and program arguments to ground.
     */
    public void ground(ProgramPart programPart, ProgramPart... programParts) {
        ProgramPart[] parts = (ProgramPart[]) programPart.toArray(1 + programParts.length);
        System.arraycopy(programParts, 0, parts, 1, programParts.length);

        checkError(Clingo.INSTANCE.clingo_control_ground(
                this.control,
                parts, new NativeSize(parts.length),
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
     * @return a solve-handle to interact with
     */
    public SolveHandle solve() {
        return solve(Collections.emptyList(), null, SolveMode.YIELD);
    }

    /**
     * Starts a search.
     *
     * @param assumptions Collection of symbols
     * @return a solve-handle to interact with
     */
    public SolveHandle solve(Collection<Symbol> assumptions) {
        return solve(assumptions, null, SolveMode.YIELD);
    }

    /**
     * Starts an asynchronous search.
     *
     * @param callback Optional callbacks for intercepting models, lower bounds during optimization,
     *                 statistics updates, or the end of the search (implement {@link SolveEventCallback}.
     * @return a solve-handle to interact with
     */
    public SolveHandle solve(SolveEventCallback callback) {
        return solve(Collections.emptyList(), callback, SolveMode.ASYNC);
    }

    /**
     * Starts an asynchronous search.
     *
     * @param assumptions Collection of symbols
     * @param callback Optional callbacks for intercepting models, lower bounds during optimization,
     *                 statistics updates, or the end of the search (implement {@link SolveEventCallback}.
     * @return a solve-handle to interact with
     */
    public SolveHandle solve(Collection<Symbol> assumptions, SolveEventCallback callback) {
        return solve(assumptions, callback, SolveMode.ASYNC);
    }

    /**
     * Starts a search.
     *
     * @param assumptions Collection of symbols
     * @param callback    Optional callbacks for intercepting models, lower bounds during optimization,
     *                    statistics updates, or the end of the search (implement {@link SolveEventCallback}.
     * @param solveMode   whether the search is blocking / non-blocking
     * @return a solve-handle to interact with
     */
    public SolveHandle solve(Collection<Symbol> assumptions, SolveEventCallback callback, SolveMode solveMode) {
        // TODO: implement call back data?
        int[] assumptionLiterals = assumptions.stream()
                .map(getSymbolicAtoms()::getSymbolicAtom)
                .mapToInt(SymbolicAtom::getLiteral).toArray();
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_solve(
                control,
                solveMode.getValue(),
                assumptionLiterals,
                new NativeSize(assumptionLiterals.length),
                callback,
                control,
                pointerByReference
        ));
        return new SolveHandle(pointerByReference.getValue());
    }

    public SolveHandle solve(int[] assumptions, SolveEventCallback callback, SolveMode solveMode) {
        // TODO: remove this method
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_solve(
                control,
                solveMode.getValue(),
                assumptions,
                new NativeSize(assumptions.length),
                callback,
                control,
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
        // TODO: own class for literal?
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
        checkError(Clingo.INSTANCE.clingo_control_register_observer(control, observer, replace, control));
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
        checkError(Clingo.INSTANCE.clingo_control_register_propagator(control, propagator, control, sequentially));
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

    /**
     * @return a `Backend` object providing a low level interface to extend a logic program.
     */
    public Backend getBackend() {
        PointerByReference backendRef = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_backend(control, backendRef));
        return new Backend(backendRef.getValue());
    }

    /**
     * @return An object to inspect the symbolic atoms.
     */
    public SymbolicAtoms getSymbolicAtoms() {
        PointerByReference symbolicAtomsRef = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_symbolic_atoms(control, symbolicAtomsRef));
        return new SymbolicAtoms(symbolicAtomsRef.getValue());
    }

    /**
     * @return The theory atoms in a program.
     */
    public TheoryAtoms getTheoryAtoms() {
        PointerByReference pointerByReference = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_theory_atoms(control, pointerByReference));
        return new TheoryAtoms(pointerByReference.getValue());
    }

    /**
     * @return Object to change the configuration.
     */
    public Configuration getConfiguration() {
        PointerByReference configurationRef = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_configuration(control, configurationRef));
        return new Configuration(configurationRef.getValue());
    }

    /**
     * The statistics correspond to the `--stats` output of clingo. The detail of the
     * statistics depends on what level is requested on the command line. Furthermore,
     * there are some functions like `Control.release_external` that start a new
     * solving step resetting the current step statistics. It is best to access the
     * statistics right after solving.
     * <p>
     * This property is only available in clingo.
     *
     * @return An object containing solve statistics of the last solve call.
     */
    public Statistics getStatistics() {
        PointerByReference statisticsRef = new PointerByReference();
        checkError(Clingo.INSTANCE.clingo_control_statistics(control, statisticsRef));
        return new Statistics(statisticsRef.getValue());
    }

    /**
     * Frees the native control object.
     * This java object must not be used after calling this method.
     */
    @Override
    public void close() {
        Clingo.INSTANCE.clingo_control_free(control);
    }

    public Pointer getPointer() {
        return control;
    }
}
