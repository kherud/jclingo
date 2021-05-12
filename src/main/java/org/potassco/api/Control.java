package org.potassco.api;

import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.enums.TruthValue;
import org.potassco.jna.BaseClingo;
import org.potassco.jna.GroundCallback;
import org.potassco.jna.PartSt;
import org.potassco.jna.PropagatorSt;
import org.potassco.jna.SizeT;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;

/**
 * Intended to hold the control pointer and to free
 * the according memory in clingo.
 * clingo_control_new
 * clingo_control_free
 * 
 * @author Josef Schneeberger
 *
 */
public class Control implements AutoCloseable {
	private String name;
	private String logicProgram;
	private Pointer control;

	/**
	 * may not be called - retrieves no control pointer
	 */
	private Control() {
		super();
		this.name = "base";
	}

	public Control(String[] arguments) {
		this();
		Pointer logger = null;
		Pointer loggerData = null;
		int messageLimit = 20;
		this.control = BaseClingo.control(arguments, logger, loggerData, messageLimit);
	}

	public Control(String name, String[] arguments, String logicProgram) {
		this(arguments);
		this.name = name;
		this.logicProgram = logicProgram;
	}

	@Override
	public void close() throws Exception {
		BaseClingo.controlFree(this.control);
        System.out.println("Freed clingo control");
	}

	// clingo_control_new
	public Pointer control(String[] arguments, Pointer logger, Pointer loggerData, int messageLimit) {
		return BaseClingo.control(arguments, logger, loggerData, messageLimit);
	}

	/**
	 * Free a control object created with {@link BaseClingo#controlNew(String[])}.
	 * 
	 * @param control the target
	 */
	// TODO: make invisible / remove if close is working properly
	public void free() {
		BaseClingo.controlFree(this.control);
	}

	/**
	 * Extend the logic program with a program in a file.
	 * 
	 * @param control the target
	 * @param file    path to the file
	 */
	public void load(String file) {
		BaseClingo.controlLoad(this.control, file);
	}

	/**
	 * Extend the logic program with the given non-ground logic program in string
	 * form.
	 * <p>
	 * This function puts the given program into a block of form:
	 * <tt>\#program name(parameters).</tt>
	 * <p>
	 * After extending the logic program, the corresponding program parts are
	 * typically grounded with ::clingo_control_ground.
	 * 
	 * @param name           name of the program block
	 * @param parameters     string array of parameters of the program block
	 * @param program        string representation of the program
	 */
	public void add(String name, String[] parameters, String program) {
		BaseClingo.controlAdd(this.control, name, parameters, program);
	}

	/**
	 * Ground the selected @link ::clingo_part parts @endlink of the current
	 * (non-ground) logic program.
	 * <p>
	 * After grounding, logic programs can be solved with ::clingo_control_solve().
	 * <p>
	 * 
	 * @note Parts of a logic program without an explicit <tt>\#program</tt>
	 *       specification are by default put into a program called `base` without
	 *       arguments.
	 * @param parts                array of parts to ground
	 * @param groundCallback      callback to implement external functions
	 * @param groundCallbackData user data for ground_callback
	 */
	public void ground(PartSt[] parts, GroundCallback groundCallback,
			Pointer groundCallbackData) {
		BaseClingo.controlGround(this.control, parts, groundCallback, groundCallbackData);
	}

	/*
	 * ***************** Solving Functions *****************
	 */

	/**
	 * Solve the currently @link ::clingo_control_ground grounded @endlink logic
	 * program enumerating its models.
	 * <p>
	 * See the @ref Solution module for more information.
	 * 
	 * @param mode            configures the search mode
	 * @param assumptions     array of assumptions to solve under
	 * @param assumptionsSize number of assumptions
	 * @param notify          the event handler to register
	 * @param data            the user data for the event handler
	 * @return handle to the current search to enumerate models
	 */
	public Pointer solve(SolveMode mode, Pointer assumptions, SizeT assumptionsSize,
			SolveEventCallback notify, Pointer data) {
		return BaseClingo.controlSolve(this.control, mode, assumptions, assumptionsSize, notify, data);
	}

	/**
	 * Clean up the domains of the grounding component using the solving component's
	 * top level assignment.
	 * <p>
	 * This function removes atoms from domains that are false and marks atoms as
	 * facts that are true. With multi-shot solving, this can result in smaller
	 * groundings because less rules have to be instantiated and more
	 * simplifications can be applied.
	 * <p>
	 * 
	 * @note It is typically not necessary to call this function manually because
	 *       automatic cleanups at the right time are enabled by default.
	 *
	 * @see clingo_control_get_enable_cleanup()
	 * @see clingo_control_set_enable_cleanup()
	 */
	public void controlCleanup() {
		BaseClingo.controlCleanup(this.control);
	}

	/**
	 * Assign a truth value to an external atom.
	 * <p>
	 * If a negative literal is passed, the corresponding atom is assigned the
	 * inverted truth value.
	 * <p>
	 * If the atom does not exist or is not external, this is a noop.
	 * 
	 * @param literal literal to assign
	 * @param value   the truth value
	 */
	public void assignExternal(int literal, TruthValue value) {
		BaseClingo.controlAssignExternal(this.control, literal, value);
	}

	/**
	 * Release an external atom.
	 * <p>
	 * If a negative literal is passed, the corresponding atom is released.
	 * <P>
	 * After this call, an external atom is no longer external and subject to
	 * program simplifications. If the atom does not exist or is not external, this
	 * is a noop.
	 * 
	 * @param literal literal to release
	 */
	public void releaseExternal(int literal) {
		BaseClingo.controlReleaseExternal(this.control, literal);
	}

	/**
	 * Register a custom propagator with the control object.
	 * <p>
	 * If the sequential flag is set to true, the propagator is called sequentially
	 * when solving with multiple threads.
	 * <p>
	 * See the @ref PropagatorSt module for more information.
	 * 
	 * @param propagator the propagator
	 * @param data       user data passed to the propagator functions
	 * @param sequential whether the propagator should be called sequentially
	 */
	public void registerPropagator(PropagatorSt propagator, Pointer data, boolean sequential) {
		BaseClingo.controlRegisterPropagator(this.control, propagator, data, sequential);
	}

	/**
	 * Check if the solver has determined that the internal program representation
	 * is conflicting.
	 * <p>
	 * If this function returns true, solve calls will return immediately with an
	 * unsatisfiable solve result. Note that conflicts first have to be detected,
	 * e.g. - initial unit propagation results in an empty clause, or later if an
	 * empty clause is resolved during solving. Hence, the function might return
	 * false even if the problem is unsatisfiable.
	 * 
	 * @return
	 */
	public boolean isConflicting() {
		return BaseClingo.controlIsConflicting(this.control);
	}

	/**
	 * Get a statistics object to inspect solver statistics.
	 * <p>
	 * StatisticsSt are updated after a solve call.
	 * <p>
	 * See the @ref StatisticsSt module for more information.
	 * <p>
	 * 
	 * @attention The level of detail of the statistics depends on the stats option
	 *            (which can be set using @ref ConfigurationSt module or passed as an
	 *            option when @link clingo_control_new creating the control
	 *            object@endlink). The default level zero only provides basic
	 *            statistics, level one provides extended and accumulated
	 *            statistics, and level two provides per-thread statistics.
	 *            Furthermore, the statistics object is best accessed right after
	 *            solving. Otherwise, not all of its entries have valid values.
	 * @return the statistics object
	 */
	public Pointer statistics() {
		return BaseClingo.controlStatistics(this.control);
	}

	/**
	 * Interrupt the active solve call (or the following solve call right at the
	 * beginning).
	 */
	public void interrupt() {
		BaseClingo.controlInterrupt(this.control);
	}

	/**
	 * Get low-level access to clasp.
	 * <p>
	 * 
	 * @attention This function is intended for experimental use only and not part
	 *            of the stable API.
	 *            <p>
	 *            This function may return a <code>nullptr</code>. Otherwise, the
	 *            returned pointer can be casted to a ClaspFacade pointer.
	 * @return clasp pointer to the ClaspFacade object (may be <code>nullptr</code>)
	 */
	public Pointer claspFacade() {
		return BaseClingo.controlClaspFacade(this.control);
	}

	/**
	 * Get a configuration object to change the solver configuration.
	 * <p>
	 * See the @ref ConfigurationSt module for more information.
	 * 
	 * @return the configuration object
	 */
	public Configuration configuration() {
		Pointer conf = BaseClingo.controlConfiguration(this.control);
		int key = BaseClingo.configurationRoot(conf);
		return new Configuration(conf, key);
	}

	/**
	 * Configure how learnt constraints are handled during enumeration.
	 * <p>
	 * If the enumeration assumption is enabled, then all information learnt from
	 * the solver's various enumeration modes is removed after a solve call. This
	 * includes enumeration of cautious or brave consequences, enumeration of answer
	 * sets with or without projection, or finding optimal models, as well as
	 * clauses added with clingo_solve_control_add_clause().
	 * <p>
	 * 
	 * @attention For practical purposes, this option is only interesting for
	 *            single-shot solving or before the last solve call to squeeze out a
	 *            tiny bit of performance. Initially, the enumeration assumption is
	 *            enabled.
	 * @param enable whether to enable the assumption
	 */
	public void setEnableEnumerationAssumption(boolean enable) {
		BaseClingo.controlSetEnableEnumerationAssumption(this.control, enable);
	}

	/**
	 * Check whether the enumeration assumption is enabled.
	 * <p>
	 * See ::clingo_control_set_enable_enumeration_assumption().
	 * 
	 * @return
	 */
	public boolean controlGetEnableEnumerationAssumption() {
		return BaseClingo.controlGetEnableEnumerationAssumption(this.control);
	}

	/**
	 * Enable automatic cleanup after solving.
	 * <p>
	 * 
	 * @note Cleanup is enabled by default.
	 *       <p>
	 * @see clingo_control_cleanup()
	 * @see clingo_control_get_enable_cleanup()
	 * @param enable whether to enable cleanups
	 */
	public void setEnableCleanup(boolean enable) {
		BaseClingo.controlSetEnableCleanup(this.control, enable);
	}

	/**
	 * Check whether automatic cleanup is enabled.
	 * <p>
	 * See ::clingo_control_set_enable_cleanup().
	 * <p>
	 * 
	 * @see clingo_control_cleanup()
	 * @see clingo_control_set_enable_cleanup()
	 * @return whether automatic cleanup is enabled.
	 */
	public boolean controlGetEnableCleanup() {
		return BaseClingo.controlGetEnableCleanup(this.control);
	}

	// Program Inspection Functions

	/**
	 * Return the symbol for a constant definition of form:
	 * <tt>\#const name = symbol</tt>.
	 * 
	 * @param name of the constant
	 * @return the resulting symbol
	 */
	public int getConst(String name) {
		return BaseClingo.controlGetConst(this.control, name);
	}

	/**
	 * Check if there is a constant definition for the given constant.
	 * <p>
	 * 
	 * @see clingo_control_get_const()
	 * @param name the name of the constant
	 * @return whether a matching constant definition exists
	 */
	public boolean hasConst(String name) {
		return BaseClingo.controlHasConst(this.control, name);
	}

	/**
	 * Get an object to inspect symbolic atoms (the relevant Herbrand base) used for
	 * grounding.
	 * <p>
	 * See the @ref SymbolicAtoms module for more information.
	 * 
	 * @return
	 */
	public Pointer symbolicAtoms() {
		return BaseClingo.controlSymbolicAtoms(this.control);
	}

	/**
	 * Get an object to inspect theory atoms that occur in the grounding.
	 * <p>
	 * See the @ref TheoryAtomsSt module for more information.
	 * 
	 * @return the theory atoms object
	 */
	public Pointer theoryAtoms() {
		return BaseClingo.controlTheoryAtoms(this.control);
	}

	/**
	 * Register a program observer with the control object.
	 * 
	 * @param observer the observer to register
	 * @param replace  just pass the grounding to the observer but not the solver
	 * @param data     user data passed to the observer functions
	 * @return
	 */
	public void registerObserver(Pointer observer, boolean replace, Pointer data) {
		BaseClingo.controlRegisterObserver(this.control, observer, replace, data);
	}

	// Program Modification Functions

	/**
	 * Get an object to add ground directives to the program.
	 * <p>
	 * See the @ref ProgramBuilderSt module for more information.
	 * 
	 * @return the backend object
	 */
	public Backend backend() {
		return new Backend(BaseClingo.controlBackend(this.control));
	}

	/**
	 * Get an object to add non-ground directives to the program.
	 * <p>
	 * See the @ref ProgramBuilderSt module for more information.
	 * 
	 * @return the program builder object
	 */
	public Pointer programBuilder() {
		return BaseClingo.controlProgramBuilder(this.control);
	}

	public void ground() {
        PartSt[] parts = new PartSt[1];
        parts[0] = new PartSt(name, null, 0L);
        BaseClingo.controlGround(this.control, parts, null, null);
	}

	public void ground(GroundCallback groundCallbackT) {
		// TODO Auto-generated method stub
		
	}

	public void solve(SolveMode solveMode, Pointer assumptions, SolveEventCallback notify, Pointer data) {
        SizeT size = new SizeT();
		BaseClingo.controlSolve(this.control, solveMode, assumptions, size, notify, data);
	}

	public Solution solve() {
		Solution solution = new Solution();
        SizeT size = new SizeT();
		BaseClingo.controlSolve(this.control, SolveMode.YIELD, null, size, new SolveEventCallback() {
			
			@Override
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				// TODO Auto-generated method stub
				return false;
			}
		}, null);
		return solution;
	}

	public Solution solve2() throws ClingoException {
		Solution solution = new Solution();
		SolveEventCallback cb = new SolveEventCallback() {
			public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
				SolveEventType t = SolveEventType.fromValue(type);
				switch (t) {
				case MODEL:
					long[] symbols = BaseClingo.modelSymbols(event, ShowType.SHOWN);
					for (int i = 0; i < symbols.length; ++i) {
						String symbol = BaseClingo.symbolToString(symbols[i]);
						solution.addSymbol(symbol);
					}
					break;
				case STATISTICS:
					break;
				case UNSAT:
					break;
				case FINISH:
					// Pointer<Integer> p_event = (Pointer<Integer>) event;
					// handler.onFinish(new SolveResult(p_event.get()));
					// goon.set(true);
					return true;
				}
				return true;
			}
		};
		SizeT size = new SizeT();
		Pointer handle = BaseClingo.controlSolve(control, SolveMode.ASYNC, null, size , cb, null);
		BaseClingo.solveHandleClose(handle);
		// clean up
		BaseClingo.controlFree(control);
		return solution;
	}

}
