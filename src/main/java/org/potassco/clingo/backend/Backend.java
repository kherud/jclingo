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

package org.potassco.clingo.backend;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.solving.TruthValue;
import org.potassco.clingo.symbol.Number;
import org.potassco.clingo.symbol.Symbol;
import org.potassco.clingo.symbol.Text;
import org.potassco.clingo.theory.TheorySequenceType;

/**
 * Backend object providing a low level interface to extend a logic program.
 * This class allows for adding statements in ASPIF format.
 * The <code>Backend</code> is a context manager and should be used with Java's <code>try with</code> statement.
 * Alternatively it must be closed manually.
 */
public class Backend implements AutoCloseable {

	private final Pointer backend;

	public Backend(Pointer backend) {
		this.backend = backend;
		Clingo.check(Clingo.INSTANCE.clingo_backend_begin(backend));
	}

	/**
	 * Add an edge directive to the program.
	 */
	public void addAcycEdge(int nodeU, int nodeV, int[] conditions) {
		Clingo.check(Clingo.INSTANCE.clingo_backend_acyc_edge(backend, nodeU, nodeV, conditions, new NativeSize(conditions.length)));
	}

	/**
	 * Add assumptions to the program.
	 */
	public void addAssume(int[] literals) {
		Clingo.check(Clingo.INSTANCE.clingo_backend_assume(backend, literals, new NativeSize(literals.length)));
	}

	/**
	 * Return a fresh program atom.
	 *
	 * @return The program atom representing the atom.
	 */
	public int addAtom() {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_add_atom(backend, null, intByReference));
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
	public int addAtom(Symbol symbol) {
		IntByReference intByReference = new IntByReference();
		LongByReference longByReference = new LongByReference(symbol.getLong());
		Clingo.check(Clingo.INSTANCE.clingo_backend_add_atom(backend, longByReference, intByReference));
		return intByReference.getValue();
	}

	/**
	 * Mark a program atom as external.
	 *
	 * @param atom The program atom to mark as external.
	 */
	public void addExternal(int atom) {
		Clingo.check(Clingo.INSTANCE.clingo_backend_external(backend, atom, ExternalType.FREE.getValue()));
	}

	/**
	 * Mark a program atom as external fixing its truth value.
	 * Can also be used to release an external atom using {@link TruthValue#FREE}.
	 *
	 * @param atom         The program atom to mark as external.
	 * @param externalType The truth value.
	 */
	public void addExternal(int atom, ExternalType externalType) {
		Clingo.check(Clingo.INSTANCE.clingo_backend_external(backend, atom, externalType.getValue()));
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
		Clingo.check(Clingo.INSTANCE.clingo_backend_heuristic(
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
		//TODO: JNA exception "Structure array elements must use contiguous memory"
		// is there a better way than to recreate a contiguous array?
		WeightedLiteral[] contiguousArray = (WeightedLiteral[]) (new WeightedLiteral()).toArray(literals.length);
		for (int i = 0; i < literals.length; i++) {
			contiguousArray[i].literal = literals[i].literal;
			contiguousArray[i].weight = literals[i].weight;
		}
		Clingo.check(Clingo.INSTANCE.clingo_backend_minimize(backend, priority, contiguousArray, new NativeSize(literals.length)));
	}

	/**
	 * Add a project statement to the program.
	 *
	 * @param atoms List of program atoms to project on.
	 */
	public void addProject(int[] atoms) {
		Clingo.check(Clingo.INSTANCE.clingo_backend_project(backend, atoms, new NativeSize(atoms.length)));
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
		Clingo.check(Clingo.INSTANCE.clingo_backend_rule(
				backend,
				choice ? (byte) 1 : 0,
				head,
				new NativeSize(head.length),
				body,
				new NativeSize(body.length)
		));
	}

	/**
	 * Add a disjunctive or choice rule with one weight constraint with a lower bound in the body to the program.
	 *
	 * @param head       The program atoms forming the rule head.
	 * @param lowerBound The lower bound.
	 * @param body       The pairs of program literals and weights forming the elements of the weight constraint.
	 * @param choice     Whether to add a disjunctive or choice rule.
	 */
	public void addWeightRule(int[] head, int lowerBound, WeightedLiteral[] body, boolean choice) {
		//TODO: JNA exception "Structure array elements must use contiguous memory"
		// is there a better way than to recreate a contiguous array?
		WeightedLiteral[] contiguousArray = (WeightedLiteral[]) (new WeightedLiteral()).toArray(body.length);
		for (int i = 0; i < body.length; i++) {
			contiguousArray[i].literal = body[i].literal;
			contiguousArray[i].weight = body[i].weight;
		}
		Clingo.check(Clingo.INSTANCE.clingo_backend_weight_rule(
				backend,
				choice ? (byte) 1 : 0,
				head,
				new NativeSize(head.length),
				lowerBound,
				contiguousArray,
				new NativeSize(body.length)
		));
	}

	/**
	 * Add a numeric theory term.
	 *
	 * @param number the value of the term
	 * @return the resulting term id
	 */
	public int addTheoryTermNumber(Number number) {
		return addTheoryTermNumber(number.getNumber());
	}

	/**
	 * Add a numeric theory term.
	 *
	 * @param number the value of the term
	 * @return the resulting term id
	 */
	public int addTheoryTermNumber(int number) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_term_number(
				backend,
				number,
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory term representing a string.
	 *
	 * @param string the value of the term
	 * @return the resulting term id
	 */
	public int addTheoryTermString(Text string) {
		return addTheoryTermString(string.getText());
	}

	/**
	 * Add a theory term representing a string.
	 *
	 * @param string the value of the term
	 * @return the resulting term id
	 */
	public int addTheoryTermString(String string) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_term_string(
				backend,
				string,
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory term representing a sequence of theory terms.
	 *
	 * @param type      the type of the sequence
	 * @param arguments the term ids of the terms in the sequence
	 * @return the resulting term id
	 */
	public int addTheoryTermSequence(TheorySequenceType type, int[] arguments) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_term_sequence(
				backend,
				type.getValue(),
				arguments,
				new NativeSize(arguments.length),
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory term representing a function.
	 *
	 * @param name      the name of the function
	 * @param arguments an array of term ids for the theory terms in the arguments
	 * @return the resulting term id
	 */
	public int addTheoryTermFunction(String name, int[] arguments) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_term_function(
				backend,
				name,
				arguments,
				new NativeSize(arguments.length),
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Convert the given symbol into a theory term.
	 *
	 * @param symbol the symbol to convert
	 * @return the resulting term id
	 */
	public int addTheoryTermSymbol(Symbol symbol) {
		return addTheoryTermSymbol(symbol.getLong());
	}

	/**
	 * Convert the given symbol into a theory term.
	 *
	 * @param symbol the symbol to convert
	 * @return the resulting term id
	 */
	public int addTheoryTermSymbol(long symbol) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_term_symbol(
				backend,
				symbol,
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory atom element.
	 *
	 * @param tuple     the array of term ids representing the tuple
	 * @param condition an array of program literals representing the condition
	 * @return the resulting element id
	 */
	public int addTheoryElement(int[] tuple, int[] condition) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_element(
				backend,
				tuple,
				new NativeSize(tuple.length),
				condition,
				new NativeSize(condition.length),
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory atom without a guard.
	 * <p>
	 * If atom is set to zero the theory atom is a directive and otherwise the theory atom receives the given atom id.
	 * <p>
	 * In case an equivalent theory atom already exists, the given atom id is ignored.
	 * <p>
	 * To declare a defined theory atom, a rule defining the program atom should be added. Otherwise, the theory atom
	 * is considered an external body occurrence.
	 *
	 * @param termId   the term id of the term associated with the theory atom
	 * @param elements an array of element ids for the theory atoms's elements
	 * @param atom     a program atom or zero for theory directives
	 * @return the final program atom of the theory atom
	 */
	public int addTheoryAtom(int termId, int[] elements, int atom) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_atom(
				backend,
				atom,
				termId,
				elements,
				new NativeSize(elements.length),
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory atom without a guard. The theory atom receives a fresh atom id.
	 *
	 * @param termId   the term id of the term associated with the theory atom
	 * @param elements an array of element ids for the theory atoms's elements
	 * @return the final program atom of the theory atom
	 */
	public int addTheoryAtom(int termId, int[] elements) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_atom(
				backend,
				0xffffffff,
				termId,
				elements,
				new NativeSize(elements.length),
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory atom with a guard.
	 * <p>
	 * If atom is set to zero the theory atom is a directive and otherwise the theory atom receives the given atom id.
	 * <p>
	 * In case an equivalent theory atom already exists, the given atom id is ignored.
	 * <p>
	 * To declare a defined theory atom, a rule defining the program atom should be added. Otherwise, the theory atom
	 * is considered an external body occurrence.
	 *
	 * @param termId          the term id of the term associated with the theory atom
	 * @param elements        an array of element ids for the theory atoms's elements
	 * @param operatorName    the string representation of a theory operator
	 * @param rightHandSideId the term id of the right hand side term
	 * @param atom            a program atom  or zero for theory directives
	 * @return the final program atom of the theory atom
	 */
	public int addTheoryAtomWithGuard(int termId, int[] elements, String operatorName, int rightHandSideId, int atom) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_atom_with_guard(
				backend,
				atom,
				termId,
				elements,
				new NativeSize(elements.length),
				operatorName,
				rightHandSideId,
				intByReference
		));
		return intByReference.getValue();
	}

	/**
	 * Add a theory atom with a guard. The theory atom receives a fresh atom id.
	 *
	 * @param termId          the term id of the term associated with the theory atom
	 * @param elements        an array of element ids for the theory atoms's elements
	 * @param operatorName    the string representation of a theory operator
	 * @param rightHandSideId the term id of the right hand side term
	 * @return the final program atom of the theory atom
	 */
	public int addTheoryAtomWithGuard(int termId, int[] elements, String operatorName, int rightHandSideId) {
		IntByReference intByReference = new IntByReference();
		Clingo.check(Clingo.INSTANCE.clingo_backend_theory_atom_with_guard(
				backend,
				0xffffffff,
				termId,
				elements,
				new NativeSize(elements.length),
				operatorName,
				rightHandSideId,
				intByReference
		));
		return intByReference.getValue();
	}

	@Override
	public void close() {
		Clingo.check(Clingo.INSTANCE.clingo_backend_end(backend));
	}
}
