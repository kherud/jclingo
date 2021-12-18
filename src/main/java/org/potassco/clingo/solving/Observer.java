package org.potassco.clingo.solving;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.potassco.clingo.internal.NativeSize;

import java.util.Arrays;
import java.util.List;

/**
 * An instance of this struct has to be registered with a solver
 * to observe ground directives as they are passed to the solver.
 * <p>
 * This interface is closely modeled after the aspif format.
 * For more information please refer to the specification of the aspif format.
 * <p>
 * Not all callbacks have to be implemented and can be set to NULL if not needed.
 * If one of the callbacks in the struct fails, grounding is stopped.
 * If a non-recoverable clingo API call fails, a callback must return false.
 * Otherwise ::clingo_error_unknown should be set and false returned.
 * <p>
 * To implement an own observer, this class has to be inherited.
 * There must not be any public variables.
 */
public abstract class Observer extends Structure {
    /**
     * Called once in the beginning.
     * <p>
     * If the incremental flag is true, there can be multiple calls to @ref clingo_control_solve().
     *
     * @param incremental whether the program is incremental
     * @param data        user data for the callback
     * @return whether the call was successful
     */
    public boolean initProgram(boolean incremental, Pointer data) {
        return true;
    }

    /**
     * Marks the beginning of a block of directives passed to the solver.
     *
     * @param data user data for the callback
     * @return whether the call was successful
     */
    public boolean beginStep(Pointer data) {
        return true;
    }

    /**
     * Marks the end of a block of directives passed to the solver.
     * <p>
     * This function is called before solving starts.
     *
     * @param data user data for the callback
     * @return whether the call was successful
     */
    public boolean endStep(Pointer data) {
        return true;
    }

    /**
     * Observe rules passed to the solver.
     *
     * @param choice   determines if the head is a choice or a disjunction
     * @param head     the head atoms
     * @param headSize the number of atoms in the head
     * @param body     the body literals
     * @param bodySize the number of literals in the body
     * @param data     user data for the callback
     * @return whether the call was successful
     */
    public boolean rule(boolean choice, Pointer head, NativeSize headSize, Pointer body, NativeSize bodySize, Pointer data) {
        return true;
    }

    /**
     * Observe weight rules passed to the solver.
     *
     * @param choice     determines if the head is a choice or a disjunction
     * @param head       the head atoms
     * @param headSize   the number of atoms in the head
     * @param lowerBound the lower bound of the weight rule
     * @param body       the weighted body literals
     * @param bodySize   the number of weighted literals in the body
     * @param data       user data for the callback
     * @return whether the call was successful
     */
    public boolean weightRule(boolean choice, Pointer head, NativeSize headSize, int lowerBound, Pointer body, NativeSize bodySize, Pointer data) {
        return true;
    }

    /**
     * Observe minimize constraints (or weak constraints) passed to the solver.
     *
     * @param priority the priority of the constraint
     * @param literals the weighted literals whose sum to minimize
     * @param size     the number of weighted literals
     * @param data     user data for the callback
     * @return whether the call was successful
     */
    public boolean minimize(int priority, Pointer literals, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe projection directives passed to the solver.
     *
     * @param atoms the atoms to project on
     * @param size  the number of atoms
     * @param data  user data for the callback
     * @return whether the call was successful
     */
    public boolean project(Pointer atoms, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe shown atoms passed to the solver.
     * Facts do not have an associated aspif atom.
     * The value of the atom is set to zero.
     *
     * @param symbol the symbolic representation of the atom
     * @param atom   the aspif atom (0 for facts)
     * @param data   user data for the callback
     * @return whether the call was successful
     */
    public boolean outputAtom(long symbol, Pointer atom, Pointer data) {
        return true;
    }

    /**
     * Observe shown terms passed to the solver.
     *
     * @param symbol    the symbolic representation of the term
     * @param condition the literals of the condition
     * @param size      the size of the condition
     * @param data      user data for the callback
     * @return whether the call was successful
     */
    public boolean outputTerm(long symbol, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe shown csp variables passed to the solver.
     *
     * @param symbol    the symbolic representation of the variable
     * @param value     the value of the variable
     * @param condition the literals of the condition
     * @param size      the size of the condition
     * @param data      user data for the callback
     * @return whether the call was successful
     */
    public boolean outputCSP(long symbol, int value, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe external statements passed to the solver.
     *
     * @param atom the external atom
     * @param type the type of the external statement
     * @param data user data for the callback
     * @return whether the call was successful
     */
    public boolean external(Pointer atom, int type, Pointer data) {
        return true;
    }

    /**
     * Observe assumption directives passed to the solver.
     *
     * @param literals the literals to assume (positive literals are true and negative literals false for the next solve call)
     * @param size     the number of atoms
     * @param data     user data for the callback
     * @return whether the call was successful
     */
    public boolean assume(Pointer literals, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe heuristic directives passed to the solver.
     *
     * @param atom      the target atom
     * @param type      the type of the heuristic modification
     * @param bias      the heuristic bias
     * @param priority  the heuristic priority
     * @param condition the condition under which to apply the heuristic modification
     * @param size      the number of atoms in the condition
     * @param data      user data for the callback
     * @return whether the call was successful
     */
    public boolean heuristic(Pointer atom, int type, int bias, int priority, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe edge directives passed to the solver.
     *
     * @param nodeU     the start vertex of the edge
     * @param nodeV     the end vertex of the edge
     * @param condition the condition under which the edge is part of the graph
     * @param size      the number of atoms in the condition
     * @param data      user data for the callback
     * @return whether the call was successful
     */
    public boolean acycEdge(int nodeU, int nodeV, Pointer condition, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe numeric theory terms.
     *
     * @param termId the id of the term
     * @param number the value of the term
     * @param data   user data for the callback
     * @return whether the call was successful
     */
    public boolean theoryTermNumber(int termId, int number, Pointer data) {
        return true;
    }

    /**
     * Observe string theory terms.
     *
     * @param termId the id of the term
     * @param name   the value of the term
     * @param data   user data for the callback
     * @return whether the call was successful
     */
    public boolean theoryTermString(int termId, String name, Pointer data) {
        return true;
    }

    /**
     * Observe compound theory terms.
     * <p>
     * The name_id_or_type gives the type of the compound term:
     * - if it is -1, then it is a tuple
     * - if it is -2, then it is a set
     * - if it is -3, then it is a list
     * - otherwise, it is a function and name_id_or_type refers to the id of the name (in form of a string term)
     *
     * @param termId       the id of the term
     * @param nameIdOrType the name or type of the term
     * @param arguments    the arguments of the term
     * @param size         the number of arguments
     * @param data         user data for the callback
     * @return whether the call was successful
     */
    public boolean theoryTermCompound(int termId, int nameIdOrType, Pointer arguments, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe theory elements.
     *
     * @param elementId     the id of the element
     * @param terms         the term tuple of the element
     * @param termsSize     the number of terms in the tuple
     * @param condition     the condition of the elemnt
     * @param conditionSize the number of literals in the condition
     * @param data          user data for the callback
     * @return whether the call was successful
     */
    public boolean theoryElement(int elementId, Pointer terms, NativeSize termsSize, Pointer condition, NativeSize conditionSize, Pointer data) {
        return true;
    }

    /**
     * Observe theory atoms without guard.
     *
     * @param atomIdOrZero the id of the atom or zero for directives
     * @param termId       the term associated with the atom
     * @param elements     the elements of the atom
     * @param size         the number of elements
     * @param data         user data for the callback
     * @return whether the call was successful
     */
    public boolean theoryAtom(int atomIdOrZero, int termId, Pointer elements, NativeSize size, Pointer data) {
        return true;
    }

    /**
     * Observe theory atoms with guard.
     *
     * @param atomIdOrZero    the id of the atom or zero for directives
     * @param termId          the term associated with the atom
     * @param elements        the elements of the atom
     * @param size            the number of elements
     * @param operatorId      the id of the operator (a string term)
     * @param rightHandSideId the id of the term on the right hand side of the atom
     * @param data            user data for the callback
     * @return whether the call was successful
     */
    public boolean theoryAtomWithGuard(int atomIdOrZero, int termId, Pointer elements, NativeSize size, int operatorId, int rightHandSideId, Pointer data) {
        return true;
    }

    /**
     * JNA DECLARATIONS
     **/

    protected List<String> getFieldOrder() {
        return Arrays.asList("initProgram", "beginStep", "endStep", "rule", "weightRule", "minimize", "project",
                "outputAtom", "outputTerm", "outputCsp", "external", "assume", "heuristic", "acycEdge",
                "theoryTermNumber", "theoryTermString", "theoryTermCompound", "theoryElement", "theoryAtom",
                "theoryAtomWithGuard");
    }

    public ObserverInitProgramCallback initProgram = this::initProgram;
    public ObserverBeginStepCallback beginStep = this::beginStep;
    public ObserverEndStepCallback endStep = this::endStep;
    public ObserverRuleCallback rule = this::rule;
    public ObserverWeightRuleCallback weightRule = this::weightRule;
    public ObserverMinimizeCallback minimize = this::minimize;
    public ObserverProjectCallback project = this::project;
    public ObserverOutputAtomCallback outputAtom = this::outputAtom;
    public ObserverOutputTermCallback outputTerm = this::outputTerm;
    public ObserverOutputCspCallback outputCsp = this::outputCSP;
    public ObserverExternalCallback external = this::external;
    public ObserverAssumeCallback assume = this::assume;
    public ObserverHeuristicCallback heuristic = this::heuristic;
    public ObserverAcycEdgeCallback acycEdge = this::acycEdge;
    public ObserverTheoryTermNumberCallback theoryTermNumber = this::theoryTermNumber;
    public ObserverTheoryTermStringCallback theoryTermString = this::theoryTermString;
    public ObserverTheoryTermCompoundCallback theoryTermCompound = this::theoryTermCompound;
    public ObserverTheoryElementCallback theoryElement = this::theoryElement;
    public ObserverTheoryAtomCallback theoryAtom = this::theoryAtom;
    public ObserverTheoryAtomWithGuardCallback theoryAtomWithGuard = this::theoryAtomWithGuard;

    private interface ObserverInitProgramCallback extends Callback {
        boolean callback(boolean incremental, Pointer data);
    }

    private interface ObserverBeginStepCallback extends Callback {
        boolean callback(Pointer data);
    }

    private interface ObserverEndStepCallback extends Callback {
        boolean callback(Pointer data);
    }

    private interface ObserverRuleCallback extends Callback {
        boolean callback(boolean choice, Pointer head, NativeSize headSize, Pointer body, NativeSize bodySize, Pointer data);
    }

    private interface ObserverWeightRuleCallback extends Callback {
        boolean callback(boolean choice, Pointer head, NativeSize headSize, int lowerBound, Pointer body, NativeSize bodySize, Pointer data);
    }

    private interface ObserverMinimizeCallback extends Callback {
        boolean callback(int priority, Pointer literals, NativeSize size, Pointer data);
    }

    private interface ObserverProjectCallback extends Callback {
        boolean callback(Pointer atoms, NativeSize size, Pointer data);
    }

    private interface ObserverOutputAtomCallback extends Callback {
        boolean callback(long symbol, Pointer atom, Pointer data);
    }

    private interface ObserverOutputTermCallback extends Callback {
        boolean callback(long symbol, Pointer condition, NativeSize size, Pointer data);
    }

    private interface ObserverOutputCspCallback extends Callback {
        boolean callback(long symbol, int value, Pointer condition, NativeSize size, Pointer data);
    }

    private interface ObserverExternalCallback extends Callback {
        boolean callback(Pointer atom, int type, Pointer data);
    }

    private interface ObserverAssumeCallback extends Callback {
        boolean callback(Pointer literals, NativeSize size, Pointer data);
    }

    private interface ObserverHeuristicCallback extends Callback {
        boolean callback(Pointer atom, int type, int bias, int priority, Pointer condition, NativeSize size, Pointer data);
    }

    private interface ObserverAcycEdgeCallback extends Callback {
        boolean callback(int nodeU, int nodeV, Pointer condition, NativeSize size, Pointer data);
    }

    private interface ObserverTheoryTermNumberCallback extends Callback {
        boolean callback(int termId, int number, Pointer data);
    }

    private interface ObserverTheoryTermStringCallback extends Callback {
        boolean callback(int termId, String name, Pointer data);
    }

    private interface ObserverTheoryTermCompoundCallback extends Callback {
        boolean callback(int termId, int nameIdOrType, Pointer arguments, NativeSize size, Pointer data);
    }

    private interface ObserverTheoryElementCallback extends Callback {
        boolean callback(int elementId, Pointer terms, NativeSize termsSize, Pointer condition, NativeSize conditionSize, Pointer data);
    }

    private interface ObserverTheoryAtomCallback extends Callback {
        boolean callback(int atomIdOrZero, int termId, Pointer elements, NativeSize size, Pointer data);
    }

    private interface ObserverTheoryAtomWithGuardCallback extends Callback {
        boolean callback(int atomIdOrZero, int termId, Pointer elements, NativeSize size, int operatorId, int rightHandSideId, Pointer data);
    }

}
