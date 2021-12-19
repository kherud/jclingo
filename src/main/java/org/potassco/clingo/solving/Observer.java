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

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.potassco.clingo.backend.ExternalType;
import org.potassco.clingo.backend.HeuristicType;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.symbol.Symbol;

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
     */
    public void initProgram(boolean incremental) {

    }

    /**
     * Marks the beginning of a block of directives passed to the solver.
     */
    public void beginStep() {

    }

    /**
     * Marks the end of a block of directives passed to the solver.
     * <p>
     * This function is called before solving starts.
     */
    public void endStep() {

    }

    /**
     * Observe rules passed to the solver.
     *
     * @param choice determines if the head is a choice or a disjunction
     * @param head   the head atoms
     * @param body   the body literals
     */
    public void rule(boolean choice, int[] head, int[] body) {

    }

    /**
     * Observe weight rules passed to the solver.
     *
     * @param choice determines if the head is a choice or a disjunction
     * @param head   the head atoms
     * @param body   the weighted body literals
     */
    public void weightRule(boolean choice, int[] head, int lowerBound, WeightedLiteral[] body) {

    }

    /**
     * Observe minimize constraints (or weak constraints) passed to the solver.
     *
     * @param priority the priority of the constraint
     * @param literals the weighted literals whose sum to minimize
     */
    public void minimize(int priority, WeightedLiteral[] literals) {

    }

    /**
     * Observe projection directives passed to the solver.
     *
     * @param atoms the atoms to project on
     */
    public void project(int[] atoms) {

    }

    /**
     * Observe shown atoms passed to the solver.
     * Facts do not have an associated aspif atom.
     * The value of the atom is set to zero.
     *
     * @param symbol the symbolic representation of the atom
     * @param atom   the aspif atom (0 for facts)
     */
    public void outputAtom(Symbol symbol, int atom) {

    }

    /**
     * Observe shown terms passed to the solver.
     *
     * @param symbol    the symbolic representation of the term
     * @param condition the literals of the condition
     */
    public void outputTerm(Symbol symbol, int[] condition) {

    }

    /**
     * Observe shown csp variables passed to the solver.
     *
     * @param symbol    the symbolic representation of the variable
     * @param value     the value of the variable
     * @param condition the literals of the condition
     */
    public void outputCSP(Symbol symbol, int value, int[] condition) {

    }

    /**
     * Observe external statements passed to the solver.
     *
     * @param atom the external atom
     * @param type the type of the external statement
     */
    public void external(int atom, ExternalType type) {

    }

    /**
     * Observe assumption directives passed to the solver.
     *
     * @param literals the literals to assume (positive literals are true and negative literals false for the next solve call)
     */
    public void assume(int[] literals) {

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
    public void heuristic(int atom, HeuristicType type, int bias, int priority, int[] condition) {

    }

    /**
     * Observe edge directives passed to the solver.
     *
     * @param nodeU     the start vertex of the edge
     * @param nodeV     the end vertex of the edge
     * @param condition the condition under which the edge is part of the graph
     */
    public void acycEdge(int nodeU, int nodeV, int[] condition) {
    }

    /**
     * Observe numeric theory terms.
     *
     * @param termId the id of the term
     * @param number the value of the term
     */
    public void theoryTermNumber(int termId, int number) {

    }

    /**
     * Observe string theory terms.
     *
     * @param termId the id of the term
     * @param name   the value of the term
     */
    public void theoryTermString(int termId, String name) {

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
    public void theoryTermCompound(int termId, int nameIdOrType, int[] arguments) {

    }

    /**
     * Observe theory elements.
     *
     * @param elementId the id of the element
     * @param terms     the term tuple of the element
     * @param condition the condition of the elemnt
     */
    public void theoryElement(int elementId, int[] terms, int[] condition) {

    }

    /**
     * Observe theory atoms without guard.
     *
     * @param atomIdOrZero the id of the atom or zero for directives
     * @param termId       the term associated with the atom
     * @param elements     the elements of the atom
     */
    public void theoryAtom(int atomIdOrZero, int termId, int[] elements) {

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
    public void theoryAtomWithGuard(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId) {

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

    // TODO: data points to a control object with the current implementation, maybe callback.call should use it as input

    private interface ObserverInitProgramCallback extends Callback {
        /**
         * Called once in the beginning.
         * <p>
         * If the incremental flag is true, there can be multiple calls to @ref clingo_control_solve().
         *
         * @param incremental whether the program is incremental
         * @param data        user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(boolean incremental, Pointer data) {
            call(incremental);
            return true;
        }

        void call(boolean incremental);
    }

    private interface ObserverBeginStepCallback extends Callback {
        /**
         * Marks the beginning of a block of directives passed to the solver.
         *
         * @param data user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(Pointer data) {
            call();
            return true;
        }

        void call();
    }

    private interface ObserverEndStepCallback extends Callback {
        /**
         * Marks the end of a block of directives passed to the solver.
         * <p>
         * This function is called before solving starts.
         *
         * @param data user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(Pointer data) {
            call();
            return true;
        }

        void call();
    }

    private interface ObserverRuleCallback extends Callback {
        /**
         * Observe rules passed to the solver.
         *
         * @param choice      determines if the head is a choice or a disjunction
         * @param headPointer the head atoms
         * @param headSizeT   the number of atoms in the head
         * @param bodyPointer the body literals
         * @param bodySizeT   the number of literals in the body
         * @param data        user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(boolean choice, Pointer headPointer, NativeSize headSizeT, Pointer bodyPointer, NativeSize bodySizeT, Pointer data) {
            int headSize = headSizeT.intValue();
            int bodySize = bodySizeT.intValue();
            int[] head = headSize == 0 ? new int[0] : headPointer.getIntArray(0, headSize);
            int[] body = bodySize == 0 ? new int[0] : bodyPointer.getIntArray(0, bodySize);
            call(choice, head, body);
            return true;
        }

        void call(boolean choice, int[] head, int[] body);
    }

    private interface ObserverWeightRuleCallback extends Callback {
        /**
         * Observe weight rules passed to the solver.
         *
         * @param choice      determines if the head is a choice or a disjunction
         * @param headPointer the head atoms
         * @param headSizeT   the number of atoms in the head
         * @param lowerBound  the lower bound of the weight rule
         * @param bodyPointer the weighted body literals
         * @param bodySizeT   the number of weighted literals in the body
         * @param data        user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(boolean choice, Pointer headPointer, NativeSize headSizeT, int lowerBound, Pointer bodyPointer, NativeSize bodySizeT, Pointer data) {
            int headSize = headSizeT.intValue();
            int bodySize = bodySizeT.intValue();
            int[] head = headSize == 0 ? new int[0] : headPointer.getIntArray(0, headSize);
            WeightedLiteral[] body = new WeightedLiteral[bodySize];
            int structSize = Native.getNativeSize(WeightedLiteral.class);
            for (int i = 0; i < bodySize; i++) {
                body[i] = new WeightedLiteral(bodyPointer.share((long) i * structSize));
            }
            call(choice, head, lowerBound, body);
            return true;
        }

        void call(boolean choice, int[] head, int lowerBound, WeightedLiteral[] body);
    }

    private interface ObserverMinimizeCallback extends Callback {
        /**
         * Observe minimize constraints (or weak constraints) passed to the solver.
         *
         * @param priority        the priority of the constraint
         * @param literalsPointer the weighted literals whose sum to minimize
         * @param sizeT           the number of weighted literals
         * @param data            user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int priority, Pointer literalsPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            WeightedLiteral[] literals = new WeightedLiteral[size];
            int structSize = Native.getNativeSize(WeightedLiteral.class);
            for (int i = 0; i < size; i++) {
                literals[i] = new WeightedLiteral(literalsPointer.share((long) i * structSize));
            }
            call(priority, literals);
            return true;
        }

        void call(int priority, WeightedLiteral[] literals);
    }

    private interface ObserverProjectCallback extends Callback {
        /**
         * Observe projection directives passed to the solver.
         *
         * @param atomsPointer the atoms to project on
         * @param sizeT        the number of atoms
         * @param data         user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(Pointer atomsPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] atoms = size == 0 ? new int[0] : atomsPointer.getIntArray(0, size);
            call(atoms);
            return true;
        }

        void call(int[] atoms);
    }

    private interface ObserverOutputAtomCallback extends Callback {
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
        default boolean callback(long symbol, int atom, Pointer data) {
            call(Symbol.fromLong(symbol), atom);
            return true;
        }

        void call(Symbol symbol, int atom);
    }

    private interface ObserverOutputTermCallback extends Callback {
        /**
         * Observe shown terms passed to the solver.
         *
         * @param symbol           the symbolic representation of the term
         * @param conditionPointer the literals of the condition
         * @param sizeT            the size of the condition
         * @param data             user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(long symbol, Pointer conditionPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] condition = size == 0 ? new int[0] : conditionPointer.getIntArray(0, size);
            call(Symbol.fromLong(symbol), condition);
            return true;
        }

        void call(Symbol symbol, int[] condition);
    }

    private interface ObserverOutputCspCallback extends Callback {
        /**
         * Observe shown csp variables passed to the solver.
         *
         * @param symbol           the symbolic representation of the variable
         * @param value            the value of the variable
         * @param conditionPointer the literals of the condition
         * @param sizeT            the size of the condition
         * @param data             user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(long symbol, int value, Pointer conditionPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] condition = size == 0 ? new int[0] : conditionPointer.getIntArray(0, size);
            call(Symbol.fromLong(symbol), value, condition);
            return true;
        }

        void call(Symbol symbol, int value, int[] condition);
    }

    private interface ObserverExternalCallback extends Callback {
        /**
         * Observe external statements passed to the solver.
         *
         * @param atom the external atom
         * @param type the type of the external statement
         * @param data user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int atom, int type, Pointer data) {
            call(atom, ExternalType.fromValue(type));
            return true;
        }

        void call(int atom, ExternalType type);
    }

    private interface ObserverAssumeCallback extends Callback {
        /**
         * Observe assumption directives passed to the solver.
         *
         * @param literalsPointer the literals to assume (positive literals are true and negative literals false for the next solve call)
         * @param sizeT           the number of atoms
         * @param data            user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(Pointer literalsPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] literals = size == 0 ? new int[0] : literalsPointer.getIntArray(0, size);
            call(literals);
            return true;
        }

        void call(int[] literals);
    }

    private interface ObserverHeuristicCallback extends Callback {
        /**
         * Observe heuristic directives passed to the solver.
         *
         * @param atom             the target atom
         * @param type             the type of the heuristic modification
         * @param bias             the heuristic bias
         * @param priority         the heuristic priority
         * @param conditionPointer the condition under which to apply the heuristic modification
         * @param sizeT            the number of atoms in the condition
         * @param data             user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int atom, int type, int bias, int priority, Pointer conditionPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] condition = size == 0 ? new int[0] : conditionPointer.getIntArray(0, size);
            call(atom, HeuristicType.fromValue(type), bias, priority, condition);
            return true;
        }

        void call(int atom, HeuristicType type, int bias, int priority, int[] condition);
    }

    private interface ObserverAcycEdgeCallback extends Callback {
        /**
         * Observe edge directives passed to the solver.
         *
         * @param nodeU     the start vertex of the edge
         * @param nodeV     the end vertex of the edge
         * @param conditionPointer the condition under which the edge is part of the graph
         * @param sizeT      the number of atoms in the condition
         * @param data      user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int nodeU, int nodeV, Pointer conditionPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] condition = size == 0 ? new int[0] : conditionPointer.getIntArray(0, size);
            call(nodeU, nodeV, condition);
            return true;
        }

        void call(int nodeU, int nodeV, int[] condition);
    }

    private interface ObserverTheoryTermNumberCallback extends Callback {
        /**
         * Observe numeric theory terms.
         *
         * @param termId the id of the term
         * @param number the value of the term
         * @param data   user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int termId, int number, Pointer data) {
            call(termId, number);
            return true;
        }

        void call(int termId, int number);
    }

    private interface ObserverTheoryTermStringCallback extends Callback {
        /**
         * Observe string theory terms.
         *
         * @param termId the id of the term
         * @param name   the value of the term
         * @param data   user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int termId, String name, Pointer data) {
            call(termId, name);
            return true;
        }

        void call(int termId, String name);
    }

    private interface ObserverTheoryTermCompoundCallback extends Callback {
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
         * @param argumentsPointer    the arguments of the term
         * @param sizeT         the number of arguments
         * @param data         user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int termId, int nameIdOrType, Pointer argumentsPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] arguments = size == 0 ? new int[0] : argumentsPointer.getIntArray(0, size);
            call(termId, nameIdOrType, arguments);
            return true;
        }

        void call(int termId, int nameIdOrType, int[] arguments);
    }

    private interface ObserverTheoryElementCallback extends Callback {
        /**
         * Observe theory elements.
         *
         * @param elementId     the id of the element
         * @param termsPointer         the term tuple of the element
         * @param termsSizeT     the number of terms in the tuple
         * @param conditionPointer     the condition of the elemnt
         * @param conditionSizeT the number of literals in the condition
         * @param data          user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int elementId, Pointer termsPointer, NativeSize termsSizeT, Pointer conditionPointer, NativeSize conditionSizeT, Pointer data) {
            int termsSize = termsSizeT.intValue();
            int conditionSize = conditionSizeT.intValue();
            int[] terms = termsSize == 0 ? new int[0] : termsPointer.getIntArray(0, termsSize);
            int[] condition = conditionSize == 0 ? new int[0] : conditionPointer.getIntArray(0, conditionSize);
            call(elementId, terms, condition);
            return true;
        }

        void call(int elementId, int[] terms, int[] condition);
    }

    private interface ObserverTheoryAtomCallback extends Callback {
        /**
         * Observe theory atoms without guard.
         *
         * @param atomIdOrZero the id of the atom or zero for directives
         * @param termId       the term associated with the atom
         * @param elementsPointer     the elements of the atom
         * @param sizeT         the number of elements
         * @param data         user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int atomIdOrZero, int termId, Pointer elementsPointer, NativeSize sizeT, Pointer data) {
            int size = sizeT.intValue();
            int[] elements = size == 0 ? new int[0] : elementsPointer.getIntArray(0, size);
            call(atomIdOrZero, termId, elements);
            return true;
        }

        void call(int atomIdOrZero, int termId, int[] elements);
    }

    private interface ObserverTheoryAtomWithGuardCallback extends Callback {
        /**
         * Observe theory atoms with guard.
         *
         * @param atomIdOrZero    the id of the atom or zero for directives
         * @param termId          the term associated with the atom
         * @param elementsPointer        the elements of the atom
         * @param sizeT            the number of elements
         * @param operatorId      the id of the operator (a string term)
         * @param rightHandSideId the id of the term on the right hand side of the atom
         * @param data            user data for the callback
         * @return whether the call was successful
         */
        default boolean callback(int atomIdOrZero, int termId, Pointer elementsPointer, NativeSize sizeT, int operatorId, int rightHandSideId, Pointer data) {
            int size = sizeT.intValue();
            int[] elements = size == 0 ? new int[0] : elementsPointer.getIntArray(0, size);
            call(atomIdOrZero, termId, elements, operatorId, rightHandSideId);
            return true;
        }

        void call(int atomIdOrZero, int termId, int[] elements, int operatorId, int rightHandSideId);
    }

}
