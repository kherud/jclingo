package org.potassco.api;

import org.potassco.enums.ExternalType;
import org.potassco.enums.HeuristicType;
import org.potassco.jna.BaseClingo;

import com.sun.jna.Pointer;

public class Backend {

	private Pointer pointer;

	Backend(Pointer pointer) {
		this.pointer = pointer;
	}

	/**
	 * Prepare the backend for usage.
	 * 
	 * @param p_backend the target
	 */
	public void begin() {
		BaseClingo.backendBegin(this.pointer);
	}

	/**
	 * Finalize the backend after using it.
	 * 
	 * @param p_backend the target
	 */
	public void end() {
		BaseClingo.backendEnd(this.pointer);
	}

	/**
	 * Add a rule to the program.
	 * 
	 * @param backend  the target backend
	 * @param choice   determines if the head is a choice or a disjunction
	 * @param head     the head atoms
	 * @param headSize the number of atoms in the head
	 * @param body     body literals
	 * @param bodySize the number of literals in the body
	 */
	// TODO: Remove size parameters
	public void rule(byte choice, int head, long headSize, int body, long bodySize) {
		BaseClingo.backendRule(this.pointer, choice, head, headSize, body, bodySize);
	}

	/**
	 * Add a weight rule to the program.
	 * <p>
	 * 
	 * @attention All weights and the lower bound must be positive.
	 * @param backend    the target backend
	 * @param choice     determines if the head is a choice or a disjunction
	 * @param head       the head atoms
	 * @param headSize   the number of atoms in the head
	 * @param lowerBound the lower bound of the weight rule
	 * @param body       the weighted body literals
	 * @param bodySize   the number of weighted literals in the body
	 */
	public void weightRule(byte choice, int head, long headSize, int lowerBound, int body,
			long bodySize) {
		BaseClingo.backendWeightRule(this.pointer, choice, head, headSize, lowerBound, body, bodySize);
	}

	/**
	 * Add a minimize constraint (or weak constraint) to the program.
	 * 
	 * @param backend  the target backend
	 * @param priority the priority of the constraint
	 * @param literals the weighted literals whose sum to minimize
	 * @param size     the number of weighted literals
	 */
	public void weightMinimize(int priority, int literals, long size) {
		BaseClingo.backendWeightMinimize(this.pointer, priority, literals, size);
	}

	/**
	 * Add a projection directive.
	 * 
	 * @param backend the target backend
	 * @param atoms   the atoms to project on
	 * @param size    the number of atoms
	 */
	public void weightProject(int atoms, long size) {
		BaseClingo.backendWeightProject(this.pointer, atoms, size);
	}

	/**
	 * Add an external statement.
	 * 
	 * @param backend the target backend
	 * @param atom    the external atom
	 * @param type    the type of the external statement
	 */
	public void weightExternal(int atom, ExternalType type) {
		BaseClingo.backendWeightExternal(this.pointer, atom, null);
	}

	/**
	 * Add an assumption directive.
	 * 
	 * @param backend  the target backend
	 * @param literals the literals to assume (positive literals are true and
	 *                 negative literals false for the next solve call)
	 * @param size     the number of atoms
	 */
	public void weightAssume(int literals, long size) {
		BaseClingo.backendWeightAssume(this.pointer, literals, size);
	}

	/**
	 * Add an heuristic directive.
	 * 
	 * @param backend   the target backend
	 * @param atom      the target atom
	 * @param type      the type of the heuristic modification
	 * @param bias      the heuristic bias
	 * @param priority  the heuristic priority
	 * @param condition the condition under which to apply the heuristic
	 *                  modification
	 * @param size      the number of atoms in the condition
	 */
	public void weightHeuristic(int atom, HeuristicType type, int bias, int priority,
			int condition, long size) {
		BaseClingo.backendWeightHeuristic(this.pointer, atom, type, bias, priority, condition, size);
	}

	/**
	 * Add an edge directive.
	 * 
	 * @param backend   the target backend
	 * @param nodeU     the start vertex of the edge
	 * @param nodeV     the end vertex of the edge
	 * @param condition the condition under which the edge is part of the graph
	 * @param size      the number of atoms in the condition
	 */
	public void weightAcycEdge(int nodeU, int nodeV, int condition, long size) {
		BaseClingo.backendWeightAcycEdge(this.pointer, nodeU, nodeV, condition, size);
	}

	/**
	 * Get a fresh atom to be used in aspif directives.
	 * 
	 * @param backend the target backend
	 * @param symbol  optional symbol to associate the atom with
	 * @return the resulting atom
	 */
	public int weightAddAtom(int symbol) {
		return BaseClingo.backendWeightAddAtom(this.pointer, symbol);
	}

}
