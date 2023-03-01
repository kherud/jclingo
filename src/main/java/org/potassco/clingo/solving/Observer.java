package org.potassco.clingo.solving;

import org.potassco.clingo.backend.ExternalType;
import org.potassco.clingo.backend.HeuristicType;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.symbol.Symbol;

/**
 * An instance of this struct has to be registered with a solver
 * to observe ground directives as they are passed to the solver.
 * <p>
 * This interface is closely modeled after the aspif format.
 * For more information please refer to the specification of the aspif format.
 * <p>
 * Not all callbacks have to be implemented and can be set to NULL if not needed.
 * If one of the callbacks in the struct fails, grounding is stopped.
 * <p>
 * To implement an own observer, this class has to be inherited.
 * There must not be any public variables.
 */
public interface Observer {
    /**
     * Called once in the beginning.
     * <p>
     * If the incremental flag is true, there can be multiple calls to @ref clingo_control_solve().
     *
     * @param incremental whether the program is incremental
     */
    default void initProgram(boolean incremental) {

    }

    /**
     * Marks the beginning of a block of directives passed to the solver.
     */
    default void beginStep() {

    }

    /**
     * Marks the end of a block of directives passed to the solver.
     * <p>
     * This function is called before solving starts.
     */
    default void endStep() {

    }

    /**
     * Observe rules passed to the solver.
     *
     * @param choice determines if the head is a choice or a disjunction
     * @param head   the head atoms
     * @param body   the body literals
     */
    default void rule(boolean choice, int[] head, int[] body) {

    }

    /**
     * Observe weight rules passed to the solver.
     *
     * @param choice determines if the head is a choice or a disjunction
     * @param head   the head atoms
     * @param body   the weighted body literals
     */
    default void weightRule(boolean choice, int[] head, int lowerBound, WeightedLiteral[] body) {

    }

    /**
     * Observe minimize constraints (or weak constraints) passed to the solver.
     *
     * @param priority the priority of the constraint
     * @param literals the weighted literals whose sum to minimize
     */
    default void minimize(int priority, WeightedLiteral[] literals) {

    }

    /**
     * Observe projection directives passed to the solver.
     *
     * @param atoms the atoms to project on
     */
    default void project(int[] atoms) {

    }

    /**
     * Observe shown atoms passed to the solver.
     * Facts do not have an associated aspif atom.
     * The value of the atom is set to zero.
     *
     * @param symbol the symbolic representation of the atom
     * @param atom   the aspif atom (0 for facts)
     */
    default void outputAtom(Symbol symbol, int atom) {

    }

    /**
     * Observe shown terms passed to the solver.
     *
     * @param symbol    the symbolic representation of the term
     * @param condition the literals of the condition
     */
    default void outputTerm(Symbol symbol, int[] condition) {

    }

    /**
     * Observe external statements passed to the solver.
     *
     * @param atom the external atom
     * @param type the type of the external statement
     */
    default void external(int atom, ExternalType type) {

    }

    /**
     * Observe assumption directives passed to the solver.
     *
     * @param literals the literals to assume (positive literals are true and negative literals false for the next solve call)
     */
    default void assume(int[] literals) {

    }

    /**
     * Observe heuristic directives passed to the solver.
     *
     * @param atom      the target atom
     * @param type      the type of the heuristic modification
     * @param bias      the heuristic bias
     * @param priority  the heuristic priority
     * @param condition the condition under which to apply the heuristic modification
     */
    default void heuristic(int atom, HeuristicType type, int bias, int priority, int[] condition) {

    }

    /**
     * Observe edge directives passed to the solver.
     *
     * @param nodeU     the start vertex of the edge
     * @param nodeV     the end vertex of the edge
     * @param condition the condition under which the edge is part of the graph
     */
    default void acycEdge(int nodeU, int nodeV, int[] condition) {
    }

    /**
     * Observe numeric theory terms.
     *
     * @param termId the id of the term
     * @param number the value of the term
     */
    default void theoryTermNumber(int termId, int number) {

    }

    /**
     * Observe string theory terms.
     *
     * @param termId the id of the term
     * @param name   the value of the term
     */
    default void theoryTermString(int termId, String name) {

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
     */
    default void theoryTermCompound(int termId, int nameIdOrType, int[] arguments) {

    }

    /**
     * Observe theory elements.
     *
     * @param elementId the id of the element
     * @param terms     the term tuple of the element
     * @param condition the condition of the elemnt
     */
    default void theoryElement(int elementId, int[] terms, int[] condition) {

    }

    /**
     * Observe theory atoms without guard.
     *
     * @param atomIdOrZero the id of the atom or zero for directives
     * @param termId       the term associated with the atom
     * @param elements     the elements of the atom
     */
    default void theoryAtom(int atomIdOrZero, int termId, int[] elements) {

    }

    /**
     * Observe theory atoms with guard.
     *
     * @param atomIdOrZero    the id of the atom or zero for directives
     * @param termId          the term associated with the atom
     * @param elements        the elements of the atom
     * @param operatorId      the id of the operator (a string term)
     * @param rightHandSideId the id of the term on the right hand side of the atom
     */
    default void theoryAtomWithGuard(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId) {

    }
}
