/*
 * Copyright (C) 2021 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.potassco.clingo.solving;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.control.Control;
import org.potassco.clingo.control.ShowType;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;
import org.potassco.clingo.symbol.Symbol;

/**
 * Provides access to a model during a solve call and provides a {@link SolveControl} object to influence the running
 * search.
 * <p>
 * The string representation of a model object is similar to the output of models by clingo using the default output.
 * <p>
 * <code>Model</code> objects cannot be constructed from Java. Instead, they are obtained
 * during solving (see {@link Control#solve()}). Furthermore, the lifetime of a model
 * object is limited to the scope of the callback it was passed to or until
 * the search for the next model is started. They must not be stored for later
 * use.
 */
public class Model {

	private final MemorySegment model;

	private boolean valid = true;

	public Model(MemorySegment model) {
		this.model = model;
	}

	/**
	 * clingo reuses its model between search steps, so the solve handle marks a model as gone once the search moves on.
	 */
	void invalidate() {
		valid = false;
	}

	private void checkValid() {
		if (!valid) {
			throw new IllegalStateException("the model is only valid until the search continues");
		}
	}

	/**
	 * Efficiently check if an atom is contained in the model.
	 * The atom must be represented using a function symbol.
	 *
	 * @param atom The atom to lookup.
	 * @return Whether the given atom is contained in the model.
	 */
	public boolean contains(Symbol atom) {
		checkValid();
		return Out.getBoolean(out -> ClingoH.clingo_model_contains(model, atom.getLong(), out));
	}

	/**
	 * Extend a model with the given symbols.
	 * <p>
	 * This only has an effect if there is an underlying clingo application, which will print the added symbols.
	 *
	 * @param symbols The symbols to add to the model.
	 */
	public void extend(Collection<Symbol> symbols) {
		extend(symbols.stream().mapToLong(Symbol::getLong).toArray());
	}

	/**
	 * Extend a model with a single symbol.
	 * <p>
	 * This only has an effect if there is an underlying clingo application, which will print the added symbols.
	 *
	 * @param symbol The symbol to add to the model.
	 */
	public void extend(Symbol symbol) {
		extend(new long[] { symbol.getLong() });
	}

	private void extend(long[] symbols) {
		checkValid();
		try (Arena call = Arena.ofConfined()) {
			Clingo.check(ClingoH.clingo_model_extend(model,
					call.allocateFrom(ValueLayout.JAVA_LONG, symbols), symbols.length));
		}
	}

	/**
	 * Check if the given program literal is true.
	 *
	 * @param literal The given program literal.
	 * @return Whether the given program literal is true.
	 */
	public boolean isTrue(int literal) {
		checkValid();
		return Out.getBoolean(out -> ClingoH.clingo_model_is_true(model, literal, out));
	}

	/**
	 * Check if the given literal is a consequence.
	 * <p>
	 * While enumerating cautious or brave consequences, there is partial
	 * information about which literals are consequences. The current state of a
	 * literal can be requested using this function. If this function is used
	 * during normal model enumeration, the function just returns whether a
	 * literal is true of false in the current model.
	 *
	 * @param literal the literal to lookup
	 * @return whether the literal is a consequence
	 */
	public ConsequenceType getConsequenceType(int literal) {
		checkValid();
		return Objects.requireNonNull(ConsequenceType.fromValue(
				Out.getInt(out -> ClingoH.clingo_model_is_consequence(model, literal, out))));
	}

	/**
	 * Return the list of atoms, terms, or CSP assignments in the model.
	 * <p>
	 * Atoms are represented using functions ({@link Symbol}s), and CSP
	 * assignments are represented using functions with name <code>$</code> where the
	 * first argument is the name of the CSP variable and the second its
	 * value.
	 *
	 * @return All projected symbols.
	 */
	public Symbol[] getSymbols() {
		return getSymbols(ShowType.shown());
	}

	/**
	 * Return the list of atoms, terms, or CSP assignments in the model.
	 * <p>
	 * Atoms are represented using functions ({@link Symbol}s), and CSP
	 * assignments are represented using functions with name <code>$</code> where the
	 * first argument is the name of the CSP variable and the second its
	 * value.
	 *
	 * @return The selected symbols.
	 */
	public Symbol[] getSymbols(ShowType.Type showType) {
		return getSymbols(new ShowType(showType));
	}

	/**
	 * Return the list of atoms, terms, or CSP assignments in the model.
	 * <p>
	 * Atoms are represented using functions ({@link Symbol}s), and CSP
	 * assignments are represented using functions with name <code>$</code> where the
	 * first argument is the name of the CSP variable and the second its
	 * value.
	 *
	 * @return The selected symbols.
	 */
	public Symbol[] getSymbols(ShowType showType) {
		checkValid();
		try (Arena call = Arena.ofConfined()) {
			long size = Out.getLong(out -> ClingoH.clingo_model_symbols_size(model, showType.getBitset(), out));
			MemorySegment buffer = call.allocate(ValueLayout.JAVA_LONG, size);
			Clingo.check(ClingoH.clingo_model_symbols(model, showType.getBitset(), buffer, size));
			long[] modelSymbols = buffer.toArray(ValueLayout.JAVA_LONG);
			Symbol[] symbols = new Symbol[modelSymbols.length];
			for (int i = 0; i < modelSymbols.length; i++) {
				symbols[i] = Symbol.fromLong(modelSymbols[i]);
			}
			return symbols;
		}
	}

	@Override
	public String toString() {
		Symbol[] symbols = getSymbols(ShowType.shown());
		return Arrays.stream(symbols).map(Symbol::toString).collect(Collectors.joining(" "));
	}

	/**
	 * @return Object that allows for controlling the running search.
	 */
	public SolveControl getContext() {
		checkValid();
		return new SolveControl(Out.getPointer(out -> ClingoH.clingo_model_context(model, out)));
	}

	/**
	 * Return the list of integer cost values of the model.
	 * The return values correspond to clasp's cost output.
	 *
	 * @return array of integer costs
	 */
	public long[] getCost() {
		checkValid();
		try (Arena call = Arena.ofConfined()) {
			long size = Out.getLong(out -> ClingoH.clingo_model_cost_size(model, out));
			MemorySegment costs = call.allocate(ValueLayout.JAVA_LONG, size);
			Clingo.check(ClingoH.clingo_model_cost(model, costs, size));
			return costs.toArray(ValueLayout.JAVA_LONG);
		}
	}

	/**
	 * Get the priorities of the costs.
	 *
	 * @return the resulting priorities
	 */
	public int[] getPriorities() {
		checkValid();
		try (Arena call = Arena.ofConfined()) {
			long size = Out.getLong(out -> ClingoH.clingo_model_cost_size(model, out));
			MemorySegment priorities = call.allocate(ValueLayout.JAVA_INT, size);
			Clingo.check(ClingoH.clingo_model_priority(model, priorities, size));
			return priorities.toArray(ValueLayout.JAVA_INT);
		}
	}

	/**
	 * @return The running number of the model.
	 */
	public long getNumber() {
		checkValid();
		return Out.getLong(out -> ClingoH.clingo_model_number(model, out));
	}

	/**
	 * @return Whether the optimality of the model has been proven.
	 */
	public boolean getOptimalityProven() {
		checkValid();
		return Out.getBoolean(out -> ClingoH.clingo_model_optimality_proven(model, out));
	}

	/**
	 * @return The id of the thread which found the model.
	 */
	public int getThreadId() {
		checkValid();
		return Out.getInt(out -> ClingoH.clingo_model_thread_id(model, out));
	}

	/**
	 * @return The type of the model.
	 */
	public ModelType getType() {
		checkValid();
		return ModelType.fromValue(Out.getInt(out -> ClingoH.clingo_model_type(model, out)));
	}

	public MemorySegment getPointer() {
		checkValid();
		return model;
	}
}
