package org.potassco.api.struct;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.potassco.api.dtype.NativeSize;
import org.potassco.api.enums.ExternalType;
import org.potassco.api.enums.HeuristicType;

import java.util.Arrays;
import java.util.List;

/**
 * An instance of this struct has to be registered with a solver to observe ground directives as they are passed to the solver.
 *
 * This interface is closely modeled after the aspif format.
 * For more information please refer to the specification of the aspif format.
 *
 * Not all callbacks have to be implemented and can be set to NULL if not needed.
 * If one of the callbacks in the struct fails, grounding is stopped.
 * If a non-recoverable clingo API call fails, a callback must return false.
 * Otherwise ::clingo_error_unknown should be set and false returned.
 *
 * @author Josef Schneeberger
 */
public class GroundProgramObserver extends Structure {
	public interface ObserverInitProgramCallback {
		/**
		 * Called once in the beginning.
		 *
		 * If the incremental flag is true, there can be multiple calls to @ref clingo_control_solve().
		 *
		 * @param incremental whether the program is incremental
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(byte incremental, Pointer data);
	}

	public ObserverInitProgramCallback initProgram;

	public interface ObserverBeginStepCallback {
		/**
		 * Marks the beginning of a block of directives passed to the solver.
		 *
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer data);
	}

	public ObserverBeginStepCallback beginStep;

	public interface ObserverEndStepCallback {
		/**
		 * Marks the end of a block of directives passed to the solver.
		 *
		 * This function is called before solving starts.
		 *
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer data);
	}

	public ObserverEndStepCallback endStep;

	public interface ObserverRuleCallback {
		/**
		 * Observe rules passed to the solver.
		 *
		 * @param choice determines if the head is a choice or a disjunction
		 * @param head the head atoms
		 * @param headSize the number of atoms in the head
		 * @param body the body literals
		 * @param bodySize the number of literals in the body
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(byte choice, Pointer head, NativeSize headSize, Pointer body, NativeSize bodySize, Pointer data);
	}

	public ObserverRuleCallback rule;

	public interface ObserverWeightRuleCallback {
		/**
		 * Observe weight rules passed to the solver.
		 *
		 * @param choice determines if the head is a choice or a disjunction
		 * @param head the head atoms
		 * @param headSize the number of atoms in the head
		 * @param lowerBound the lower bound of the weight rule
		 * @param body the weighted body literals
		 * @param bodySize the number of weighted literals in the body
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(byte choice, Pointer head, NativeSize headSize, int lowerBound, Pointer body, NativeSize bodySize, Pointer data);
	}

	public ObserverWeightRuleCallback weightRule;

	public interface ObserverMinimizeCallback {
		/**
		 * Observe minimize constraints (or weak constraints) passed to the solver.
		 *
		 * @param priority the priority of the constraint
		 * @param literals the weighted literals whose sum to minimize
		 * @param size the number of weighted literals
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int priority, Pointer literals, NativeSize size, Pointer data);
	}

	public ObserverMinimizeCallback minimize;

	public interface ObserverProjectCallback {
		/**
		 * Observe projection directives passed to the solver.
		 *
		 * @param atoms the atoms to project on
		 * @param size the number of atoms
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer atoms, NativeSize size, Pointer data);
	}

	public ObserverProjectCallback project;

	public interface ObserverOutputAtomCallback {
		/**
		 * Observe shown atoms passed to the solver.
		 * \note Facts do not have an associated aspif atom.
		 * The value of the atom is set to zero.
		 *
		 * @param symbol the symbolic representation of the atom
		 * @param atom the aspif atom (0 for facts)
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer symbol, Pointer atom, Pointer data);
	}

	public ObserverOutputAtomCallback outputAtom;

	public interface ObserverOutputTermCallback {
		/**
		 * Observe shown terms passed to the solver.
		 *
		 * @param symbol the symbolic representation of the term
		 * @param condition the literals of the condition
		 * @param size the size of the condition
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer symbol, Pointer condition, NativeSize size, Pointer data);
	}

	public ObserverOutputTermCallback outputTerm;

	public interface ObserverOutputCspCallback {
		/**
		 * Observe shown csp variables passed to the solver.
		 *
		 * @param symbol the symbolic representation of the variable
		 * @param value the value of the variable
		 * @param condition the literals of the condition
		 * @param size the size of the condition
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer symbol, int value, Pointer condition, NativeSize size, Pointer data);
	}

	public ObserverOutputCspCallback outputCsp;

	public interface ObserverExternalCallback {
		/**
		 * Observe external statements passed to the solver.
		 *
		 * @param atom the external atom
		 * @param type the type of the external statement
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer atom, ExternalType type, Pointer data);
	}

	public ObserverExternalCallback external;

	public interface ObserverAssumeCallback {
		/**
		 * Observe assumption directives passed to the solver.
		 *
		 * @param literals the literals to assume (positive literals are true and negative literals false for the next solve call)
		 * @param size the number of atoms
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer literals, NativeSize size, Pointer data);
	}

	public ObserverAssumeCallback assume;

	public interface ObserverHeuristicCallback {
		/**
		 * Observe heuristic directives passed to the solver.
		 *
		 * @param atom the target atom
		 * @param type the type of the heuristic modification
		 * @param bias the heuristic bias
		 * @param priority the heuristic priority
		 * @param condition the condition under which to apply the heuristic modification
		 * @param size the number of atoms in the condition
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(Pointer atom, HeuristicType type, int bias, int priority, Pointer condition, NativeSize size, Pointer data);
	}

	public ObserverHeuristicCallback heuristic;

	public interface ObserverAcycEdgeCallback {
		/**
		 * Observe edge directives passed to the solver.
		 *
		 * @param nodeU the start vertex of the edge
		 * @param nodeV the end vertex of the edge
		 * @param condition the condition under which the edge is part of the graph
		 * @param size the number of atoms in the condition
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int nodeU, int nodeV, Pointer condition, NativeSize size, Pointer data);
	}

	public ObserverAcycEdgeCallback acycEdge;

	public interface ObserverTheoryTermNumberCallback {
		/**
		 * Observe numeric theory terms.
		 *
		 * @param termId the id of the term
		 * @param number the value of the term
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int termId, int number, Pointer data);
	}

	public ObserverTheoryTermNumberCallback theoryTermNumber;

	public interface ObserverTheoryTermStringCallback {
		/**
		 * Observe string theory terms.
		 *
		 * @param termId the id of the term
		 * @param name the value of the term
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int termId, String name, Pointer data);
	}

	public ObserverTheoryTermStringCallback theoryTermString;

	public interface ObserverTheoryTermCompoundCallback {
		/**
		 * Observe compound theory terms.
		 *
		 * The name_id_or_type gives the type of the compound term:
		 * - if it is -1, then it is a tuple
		 * - if it is -2, then it is a set
		 * - if it is -3, then it is a list
		 * - otherwise, it is a function and name_id_or_type refers to the id of the name (in form of a string term)
		 *
		 * @param termId the id of the term
		 * @param nameIdOrType the name or type of the term
		 * @param arguments the arguments of the term
		 * @param size the number of arguments
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int termId, int nameIdOrType, Pointer arguments, NativeSize size, Pointer data);
	}

	public ObserverTheoryTermCompoundCallback theoryTermCompound;

	public interface ObserverTheoryElementCallback {
		/**
		 * Observe theory elements.
		 *
		 * @param elementId the id of the element
		 * @param terms the term tuple of the element
		 * @param termsSize the number of terms in the tuple
		 * @param condition the condition of the elemnt
		 * @param conditionSize the number of literals in the condition
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int elementId, Pointer terms, NativeSize termsSize, Pointer condition, NativeSize conditionSize, Pointer data);
	}

	public ObserverTheoryElementCallback theoryElement;

	public interface ObserverTheoryAtomCallback {
		/**
		 * Observe theory atoms without guard.
		 *
		 * @param atomIdOrZero the id of the atom or zero for directives
		 * @param termId the term associated with the atom
		 * @param elements the elements of the atom
		 * @param size the number of elements
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int atomIdOrZero, int termId, Pointer elements, NativeSize size, Pointer data);
	}

	public ObserverTheoryAtomCallback theoryAtom;

	public interface ObserverTheoryAtomWithGuardCallback {
		/**
		 * Observe theory atoms with guard.
		 *
		 * @param atomIdOrZero the id of the atom or zero for directives
		 * @param termId the term associated with the atom
		 * @param elements the elements of the atom
		 * @param size the number of elements
		 * @param operatorId the id of the operator (a string term)
		 * @param rightHandSideId the id of the term on the right hand side of the atom
		 * @param data user data for the callback
		 * @return whether the call was successful
		 */
		byte callback(int atomIdOrZero, int termId, Pointer elements, NativeSize size, int operatorId, int rightHandSideId, Pointer data);
	}

	public ObserverTheoryAtomWithGuardCallback theoryAtomWithGuard;

	public GroundProgramObserver(ObserverInitProgramCallback initProgram,
								 ObserverBeginStepCallback beginStep,
                                 ObserverEndStepCallback endStep,
								 ObserverRuleCallback rule,
								 ObserverWeightRuleCallback weightRule,
                                 ObserverMinimizeCallback minimize,
								 ObserverProjectCallback project,
								 ObserverOutputAtomCallback outputAtom,
                                 ObserverOutputTermCallback outputTerm,
								 ObserverOutputCspCallback outputCsp,
                                 ObserverExternalCallback external,
								 ObserverAssumeCallback assume,
								 ObserverHeuristicCallback heuristic,
                                 ObserverAcycEdgeCallback acycEdge,
								 ObserverTheoryTermNumberCallback theoryTermNumber,
                                 ObserverTheoryTermStringCallback theoryTermString,
								 ObserverTheoryTermCompoundCallback theoryTermCompound,
                                 ObserverTheoryElementCallback theoryElement,
								 ObserverTheoryAtomCallback theoryAtom,
                                 ObserverTheoryAtomWithGuardCallback theoryAtomWithGuard) {
		super();
		this.initProgram = initProgram;
		this.beginStep = beginStep;
		this.endStep = endStep;
		this.rule = rule;
		this.weightRule = weightRule;
		this.minimize = minimize;
		this.project = project;
		this.outputAtom = outputAtom;
		this.outputTerm = outputTerm;
		this.outputCsp = outputCsp;
		this.external = external;
		this.assume = assume;
		this.heuristic = heuristic;
		this.acycEdge = acycEdge;
		this.theoryTermNumber = theoryTermNumber;
		this.theoryTermString = theoryTermString;
		this.theoryTermCompound = theoryTermCompound;
		this.theoryElement = theoryElement;
		this.theoryAtom = theoryAtom;
		this.theoryAtomWithGuard = theoryAtomWithGuard;
	}

	protected List<String> getFieldOrder() {
		return Arrays.asList("initProgram", "beginStep", "endStep", "rule", "weightRule", "minimize", "project",
				"outputAtom", "outputTerm", "outputCsp", "external", "assume", "heuristic", "acycEdge",
				"theoryTermNumber", "theoryTermString", "theoryTermCompound", "theoryElement", "theoryAtom",
				"theoryAtomWithGuard");
	}

}
