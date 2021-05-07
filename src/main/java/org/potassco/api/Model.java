package org.potassco.api;

import org.potassco.enums.ModelType;
import org.potassco.enums.ShowType;
import org.potassco.jna.BaseClingo;
import org.potassco.jna.ClingoLibrary;

import com.sun.jna.Pointer;

public class Model {

	private Pointer pointer;

	private Model() {
	}
	
	public Model(Pointer pointer) {
		this();
		this.pointer = pointer;
	}

	/* Functions for Inspecting Models */

	/**
	 * Get the type of the model.
	 * 
	 * @param model the target
	 * @return the type of the model
	 */
	public ModelType type() {
		return BaseClingo.modelType(this.pointer);
	}

	/**
	 * Get the running number of the model.
	 * 
	 * @param model the target
	 * @return the number of the model
	 */
	public long number() {
		return BaseClingo.modelNumber(this.pointer);
	}

	/**
	 * Get the number of symbols of the selected types in the model.
	 * 
	 * @param model the target
	 * @param show  which symbols to select - {@link ShowType}
	 * @return the number symbols
	 */
	private long modelSymbolsSize(ShowType show) {
		return BaseClingo.modelSymbolsSize(this.pointer, show);
	}

	/**
	 * Get the symbols of the selected types in the model.
	 * <p>
	 * 
	 * @note CSP assignments are represented using functions with name "$" where the
	 *       first argument is the name of the CSP variable and the second one its
	 *       value.
	 * @param model [in] model the target
	 * @param show  [in] show which symbols to select. Of {@link ShowType}
	 * @param size  [in] size the number of selected symbols
	 * @return the resulting symbols as an array[size] of symbol references
	 * @see clingo_model_symbols_size()
	 */
	public long[] symbols(ShowType show, long size) {
		return BaseClingo.modelSymbols(this.pointer, show, size);
	}

	/**
	 * Constant time lookup to test whether an atom is in a model.
	 *
	 * @param[in] model the target
	 * @param[in] atom the atom to lookup
	 * @param[out] contained whether the atom is contained
	 * @return whether the call was successful
	 */
	public byte contains(long atom) {
		return BaseClingo.modelContains(this.pointer, atom);
	}

	/**
	 * Check if a program literal is true in a model.
	 *
	 * @param[in] model the target
	 * @param[in] literal the literal to lookup
	 * @param[out] result whether the literal is true
	 * @return whether the call was successful
	 */
	public byte isTrue(long literal) {
		return BaseClingo.modelIsTrue(this.pointer, literal);
	}

	/**
	 * Get the number of cost values of a model.
	 *
	 * @param[in] model the target
	 * @param[out] size the number of costs
	 * @return whether the call was successful
	 */
	private long modelCostSize(Pointer model) {
		return BaseClingo.modelCostSize(model);
	}

	/**
	 * Get the cost vector of a model.
	 *
	 * @param[in] model the target
	 * @param[out] costs the resulting costs
	 * @param[in] size the number of costs
	 * @return whether the call was successful; might set one of the following error
	 *         codes: - ::clingo_error_bad_alloc - ::clingo_error_runtime if the
	 *         size is too small
	 *
	 * @see clingo_model_cost_size()
	 * @see clingo_model_optimality_proven()
	 */
	public int cost(long size) {
		return BaseClingo.modelCost(this.pointer, size);
	}

	/**
	 * Whether the optimality of a model has been proven.
	 *
	 * @param[in] model the target
	 * @param[out] proven whether the optimality has been proven
	 * @return whether the call was successful
	 *
	 * @see clingo_model_cost()
	 */
	public byte optimalityProven() {
		return BaseClingo.modelOptimalityProven(this.pointer);
	}

	/**
	 * Get the id of the solver thread that found the model.
	 *
	 * @param[in] model the target
	 * @param[out] id the resulting thread id
	 * @return whether the call was successful
	 */
	public int threadId() {
		return BaseClingo.modelThreadId(this.pointer);
	}

	/**
	 * Add symbols to the model.
	 *
	 * These symbols will appear in clingo's output, which means that this function
	 * is only meaningful if there is an underlying clingo application. Only models
	 * passed to the ::clingo_solve_event_callback_t are extendable.
	 *
	 * @param[in] model the target
	 * @param[in] symbols the symbols to add
	 * @param[in] size the number of symbols to add
	 * @return whether the call was successful
	 */
	public void extend(long symbols, long size) {
		BaseClingo.modelExtend(this.pointer, symbols, size);
		;
	}

	/* Functions for Adding Clauses */

	/**
	 * Get the associated solve control object of a model.
	 *
	 * This object allows for adding clauses during model enumeration.
	 * 
	 * @param[in] model the target
	 * @param[out] control the resulting solve control object
	 * @return whether the call was successful
	 */
	public Pointer context() {
		return BaseClingo.modelContext(this.pointer);
	}

}
