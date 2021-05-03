package org.potassco.base;

import org.potassco.cpp.clingo_h;
import org.potassco.dto.Solution;
import org.potassco.enums.ShowType;
import org.potassco.enums.SolveEventType;
import org.potassco.enums.SolveMode;
import org.potassco.enums.TruthValue;
import org.potassco.jna.ClingoLibrary;
import org.potassco.jna.Part;
import org.potassco.jna.Size;
import org.potassco.jna.SolveEventCallback;

import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

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
	private ClingoLibrary clingoLibrary;

	private Control() {
		super();
		this.name = "base";
	}

	public Control(ClingoLibrary clingoLibrary) {
		this();
		this.clingoLibrary = clingoLibrary;
	}

	public Control(String name, String logicProgram, ClingoLibrary clingoLibrary) {
		this(clingoLibrary);
		this.name = name;
		this.logicProgram = logicProgram;
		controlNew(null);
		add(name, null, 0L, logicProgram);
	}

	@Override
	public void close() throws Exception {
        clingoLibrary.clingo_control_free(this.control);
        System.out.println("Freed clingo control");
	}

	/* *******
	 * control
	 * ******* */
    
    // clingo_part_t
    // clingo_ground_callback_t
    // clingo_control_t
    
	/**
	 * Create a new control object.
	 * <p>
	 * A control object has to be freed using clingo_control_free().
	 * TODO: This will be done in the Control class.
	 * @param arguments array of command line arguments
	 * @return 
	 * @return resulting control object
	 */
	public void controlNew(String[] arguments) {
		PointerByReference parray = null;
		int argumentsLength = 0;
		if (arguments != null) {
			// https://github.com/nativelibs4java/nativelibs4java/issues/476
			StringArray sarray = new StringArray(arguments);
			parray = new PointerByReference();
			parray.setPointer(sarray);
			argumentsLength = arguments.length;
		}
		// TODO
		Pointer logger = null;
		Pointer loggerData = null;
		int messageLimit = 20;
		PointerByReference ctrl = new PointerByReference();
		@SuppressWarnings("unused")
//        clingoLibrary.clingo_control_new(null, 0, null, null, 20, controlPointer);
		boolean success = clingoLibrary.clingo_control_new(parray, argumentsLength, logger, loggerData, messageLimit, ctrl);
		this.control = ctrl.getValue();
	}

	/**
	 * Free a control object created with {@link Clingo#controlNew(String[])}.
	 * @param control the target
	 */
	// TODO: make invisible / remove if close is working properly 
	public void free() {
        clingoLibrary.clingo_control_free(this.control);
	}

	/**
	 * Extend the logic program with a program in a file.
	 * @param control the target
	 * @param file path to the file
	 */
	public void load(String file) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_load(this.control, file);
	}

	/**
	 * Extend the logic program with the given non-ground logic program in string form.
	 * <p>
	 * This function puts the given program into a block of form: <tt>\#program name(parameters).</tt>
	 * <p>
	 * After extending the logic program, the corresponding program parts are typically grounded with ::clingo_control_ground.
	 * 
	 * @param name name of the program block
	 * @param parameters string array of parameters of the program block
	 * @param parametersSize number of parameters
	 * @param program string representation of the program
	 */
	public void add(String name, String[] parameters, long parametersSize, String program) {
		if (parameters == null) {
			parameters = new String[0];
		}
		clingoLibrary.clingo_control_add(this.control, name, parameters, new Size(parametersSize), program);
	}

    /**
     * Ground the selected @link ::clingo_part parts @endlink of the current (non-ground) logic program.
     * <p>
     * After grounding, logic programs can be solved with ::clingo_control_solve().
     * <p>
     * @note Parts of a logic program without an explicit <tt>\#program</tt>
     * specification are by default put into a program called `base` without arguments.
     * @param parts array of parts to ground
     * @param parts_size size of the parts array
     * @param ground_callback callback to implement external functions
     * @param ground_callback_data user data for ground_callback
     */
    public void ground(Part[] parts, Size parts_size, Pointer ground_callback, Pointer ground_callback_data) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_ground(this.control, parts, parts_size, ground_callback, ground_callback_data);
    }

	/* *****************
	 * Solving Functions
	 * ***************** */

	/**
	 * Solve the currently @link ::clingo_control_ground grounded @endlink logic program enumerating its models.
	 * <p>
	 * See the @ref Solution module for more information.
	 * @param mode configures the search mode
	 * @param assumptions array of assumptions to solve under
	 * @param assumptionsSize number of assumptions
	 * @param notify the event handler to register
	 * @param data the user data for the event handler
	 * @return handle to the current search to enumerate models
	 */
	public Pointer solve(SolveMode mode, Pointer assumptions, int assumptionsSize,
//			SolveEventCallback notify,
			SolveEventCallback notify,
			Pointer data) {
        PointerByReference handle = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_solve(control, mode.getValue(),
				assumptions, assumptionsSize, notify, data, handle);
		return handle.getValue();
	}
	
	/**
	 * Clean up the domains of the grounding component using the solving
     * component's top level assignment.
     * <p>
     * This function removes atoms from domains that are false and marks atoms as
     * facts that are true.  With multi-shot solving, this can result in smaller
     * groundings because less rules have to be instantiated and more
     * simplifications can be applied.
     * <p>
     * @note It is typically not necessary to call this function manually because
     * automatic cleanups at the right time are enabled by default.
     *
     * @see clingo_control_get_enable_cleanup()
     * @see clingo_control_set_enable_cleanup()
	 */
	public void cleanup() {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_cleanup(this.control);
	}

	/**
	 * Assign a truth value to an external atom.
     * <p>
     * If a negative literal is passed, the corresponding atom is assigned the
     * inverted truth value.
     * <p>
     * If the atom does not exist or is not external, this is a noop.
	 * @param literal literal to assign
	 * @param value the truth value
	 */
	public void assignExternal(int literal, TruthValue value) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_assign_external(this.control, literal, value.getValue());
	}

	/**
	 * Release an external atom.
     * <p>
     * If a negative literal is passed, the corresponding atom is released.
     * <P>
     * After this call, an external atom is no longer external and subject to
     * program simplifications.  If the atom does not exist or is not external,
     * this is a noop.
	 * @param literal literal to release
	 */
	public void releaseExternal(int literal) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_release_external(this.control, literal);
	}

	/**
	 * Register a custom propagator with the control object.
     * <p>
     * If the sequential flag is set to true, the propagator is called
     * sequentially when solving with multiple threads.
     * <p>
     * See the @ref Propagator module for more information.
	 * @param propagator the propagator
	 * @param data user data passed to the propagator functions
	 * @param sequential whether the propagator should be called sequentially
	 */
	public void registerPropagator(Pointer control, Pointer propagator, Pointer data, boolean sequential) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_register_propagator(this.control, propagator, data, (byte) (sequential ? 1 : 0));
	}

	/**
	 * Check if the solver has determined that the internal program representation is conflicting.
	 * <p>
	 * If this function returns true, solve calls will return immediately with an unsatisfiable solve result.
     * Note that conflicts first have to be detected, e.g. -
     * initial unit propagation results in an empty clause,
     * or later if an empty clause is resolved during solving.
     * Hence, the function might return false even if the problem is unsatisfiable.
	 * @return 
	 */
	public boolean isConflicting() {
		byte isConflicting = clingoLibrary.clingo_control_is_conflicting(this.control);
		return isConflicting == 1;
	}

    /**
     * Get a statistics object to inspect solver statistics.
     * <p>
     * Statistics are updated after a solve call.
     * <p>
     * See the @ref Statistics module for more information.
     * <p>
     * @attention
     * The level of detail of the statistics depends on the stats option
     * (which can be set using @ref Configuration module or passed as an option when @link clingo_control_new creating the control object@endlink).
     * The default level zero only provides basic statistics,
     * level one provides extended and accumulated statistics,
     * and level two provides per-thread statistics.
     * Furthermore, the statistics object is best accessed right after solving.
     * Otherwise, not all of its entries have valid values.
     * @return the statistics object
     */
    public Pointer statistics() {
    	PointerByReference statistics = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_statistics(this.control, statistics);
		return statistics.getValue();
    }

    /**
     * Interrupt the active solve call (or the following solve call right at the beginning).
     */
    public void interrupt() {
		clingoLibrary.clingo_control_interrupt(this.control);
    }

    /**
     * Get low-level access to clasp.
     * <p>
     * @attention
     * This function is intended for experimental use only and not part of the stable API.
     * <p>
     * This function may return a <code>nullptr</code>.
     * Otherwise, the returned pointer can be casted to a ClaspFacade pointer.
     * @return clasp pointer to the ClaspFacade object (may be <code>nullptr</code>)
     */
    public Pointer claspFacade() {
    	PointerByReference claspFacade = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_clasp_facade(this.control, claspFacade);
		return claspFacade.getValue();
    }

    /**
     * Get a configuration object to change the solver configuration.
     * <p>
     * See the @ref Configuration module for more information.
     * @return the configuration object
     */
    public Pointer configuration() {
    	PointerByReference configuration = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_configuration(this.control, configuration);
		return configuration.getValue();
    }

    /**
     * Configure how learnt constraints are handled during enumeration.
     * <p>
     * If the enumeration assumption is enabled, then all information learnt from
     * the solver's various enumeration modes is removed after a solve call. This
     * includes enumeration of cautious or brave consequences, enumeration of
     * answer sets with or without projection, or finding optimal models, as well
     * as clauses added with clingo_solve_control_add_clause().
     * <p>
     * @attention For practical purposes, this option is only interesting for single-shot solving
     * or before the last solve call to squeeze out a tiny bit of performance.
     * Initially, the enumeration assumption is enabled.
     * @param enable whether to enable the assumption
     */
    public void setEnableEnumerationAssumption(boolean enable) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_set_enable_enumeration_assumption(this.control, (byte) (enable ? 1 : 0));
    }

    /**
     * Check whether the enumeration assumption is enabled.
     * <p>
     * See ::clingo_control_set_enable_enumeration_assumption().
     * @return 
     */
    public boolean getEnableEnumerationAssumption() {
		byte enabled = clingoLibrary.clingo_control_get_enable_enumeration_assumption(this.control);
		return enabled == 1;
    }

    /**
     * Enable automatic cleanup after solving.
     * <p>
     * @note Cleanup is enabled by default.
     * <p>
     * @see clingo_control_cleanup()
     * @see clingo_control_get_enable_cleanup()
     * @param enable whether to enable cleanups
    */
    public void setEnableCleanup(boolean enable) {
 		@SuppressWarnings("unused")
 		byte success = clingoLibrary.clingo_control_set_enable_cleanup(this.control, (byte) (enable ? 1 : 0));
    }

    /**
     * Check whether automatic cleanup is enabled.
     * <p>
     * See ::clingo_control_set_enable_cleanup().
     * <p>
     * @see clingo_control_cleanup()
     * @see clingo_control_set_enable_cleanup()
     * @return whether automatic cleanup is enabled.
     */
    public boolean getEnableCleanup() {
 		byte enabled = clingoLibrary.clingo_control_get_enable_cleanup(this.control);
 		return enabled == 1;
    }
    
    // Program Inspection Functions

    /**
     * Return the symbol for a constant definition of form: <tt>\#const name = symbol</tt>.
     * @param name of the constant
     * @return the resulting symbol
     */
    public int getConst(String name) {
 		IntByReference symbol = new IntByReference();
		@SuppressWarnings("unused")
 		byte success = clingoLibrary.clingo_control_get_const(this.control, name, symbol);
		return symbol.getValue();
    }

    /**
     * Check if there is a constant definition for the given constant.
     * <p>
     * @see clingo_control_get_const()
     * @param name the name of the constant
     * @return whether a matching constant definition exists
     */
    public boolean hasConst(String name) {
		ByteByReference exists = new ByteByReference();
		@SuppressWarnings("unused")
 		byte success = clingoLibrary.clingo_control_has_const(this.control, name, exists );
		return exists.getValue() == 1;
    }

    /**
     * Get an object to inspect symbolic atoms (the relevant Herbrand base) used for grounding.
     * <p>
     * See the @ref SymbolicAtoms module for more information.
     * @return
     */
    public Pointer symbolicAtoms() {
		PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_symbolic_atoms(this.control, atoms);
		return atoms.getValue();
    }

    /**
     * Get an object to inspect theory atoms that occur in the grounding.
     * <p>
     * See the @ref TheoryAtoms module for more information.
     * @return the theory atoms object
     */
    public Pointer theoryAtoms() {
    	PointerByReference atoms = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_theory_atoms(this.control, atoms);
		return atoms.getValue();
    }

    /**
     * Register a program observer with the control object.
     * @param observer the observer to register
     * @param replace just pass the grounding to the observer but not the solver
     * @param data user data passed to the observer functions
     * @return
     */
    public void registerObserver(Pointer observer, boolean replace, Pointer data) {
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_register_observer(this.control, observer, (byte) (replace ? 1 : 0), data);
    }
    
    // Program Modification Functions
    
    /**
     * Get an object to add ground directives to the program.
     * <p>
     * See the @ref ProgramBuilder module for more information.
     * @return the backend object
     */
    public Pointer backend() {
    	PointerByReference backend = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_backend(this.control, backend);
		return backend.getValue();
    }

    /**
     * Get an object to add non-ground directives to the program.
     * <p>
     * See the @ref ProgramBuilder module for more information.
     * @return the program builder object
     */
    public Pointer programBuilder() {
    	PointerByReference builder = new PointerByReference();
		@SuppressWarnings("unused")
		byte success = clingoLibrary.clingo_control_program_builder(this.control, builder);
		return builder.getValue();
    }

    // **********************************************************************************
    
    /**
	 * @param name
	 * {@link clingo_h#clingo_control_ground}
	 */
	public void ground(String name) {
        Part[] parts = new Part[1];
        parts[0] = new Part(name, null, new Size(0));
        ground(parts, new Size(1), null, null);
	}

	public Solution solve() throws ClingoException {
		Clingo clingo = Clingo.getInstance();
        Solution solution = new Solution();
        SolveEventCallback cb = new SolveEventCallback() {
            public boolean call(int type, Pointer event, Pointer data, Pointer goon) {
                SolveEventType t = SolveEventType.fromValue(type);
                switch (t) {
                    case MODEL:
                    	long size = clingo.modelSymbolsSize(event, ShowType.SHOWN);
                        solution.setSize(size);
                        long[] symbols = clingo.modelSymbols(event, ShowType.SHOWN, size);
                        for (int i = 0; i < size; ++i) {
                            long len = clingo.symbolToStringSize(symbols[i]);
                            String symbol = clingo.symbolToString(symbols[i], len);
                            solution.addSymbol(symbol.trim());
                        }
                        break;
                    case STATISTICS:
                        break;
                    case UNSAT:
                        break;
                    case FINISH:
//                        Pointer<Integer> p_event = (Pointer<Integer>) event;
//                        handler.onFinish(new SolveResult(p_event.get()));
//                        goon.set(true);
                        return true;
                }
                return true;
            }
        };
        Pointer handle = solve(SolveMode.ASYNC, null, 0, cb, null);
        clingo.solveHandleClose(handle);
        // clean up
        free();
		return solution;
    }

}
