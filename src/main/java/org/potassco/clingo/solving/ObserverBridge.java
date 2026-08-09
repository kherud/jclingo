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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.potassco.clingo.backend.ExternalType;
import org.potassco.clingo.backend.HeuristicType;
import org.potassco.clingo.backend.WeightedLiteral;
import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.NativeArrays;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.clingo_ground_program_observer;
import org.potassco.clingo.symbol.Symbol;

/**
 * Builds the {@code clingo_ground_program_observer_t} clingo expects from an {@link Observer}.
 */
public final class ObserverBridge {

    private ObserverBridge() {
    }

    /**
     * @param arena  the arena owning the struct and its stubs; clingo uses them until the control is freed
     * @param policy what to do when an observer method throws
     * @return a {@code clingo_ground_program_observer_t}
     */
    public static MemorySegment of(Observer observer, Arena arena, ErrorPolicy policy) {
        MemorySegment struct = clingo_ground_program_observer.allocate(arena);

        clingo_ground_program_observer.init_program(struct, Upcalls.stub(
                clingo_ground_program_observer.init_program.Function.class,
                (incremental, data) -> {
                    observer.initProgram(incremental);
                    return true;
                },
                clingo_ground_program_observer.init_program.descriptor(), arena, policy));

        clingo_ground_program_observer.begin_step(struct, Upcalls.stub(
                clingo_ground_program_observer.begin_step.Function.class,
                data -> {
                    observer.beginStep();
                    return true;
                },
                clingo_ground_program_observer.begin_step.descriptor(), arena, policy));

        clingo_ground_program_observer.end_step(struct, Upcalls.stub(
                clingo_ground_program_observer.end_step.Function.class,
                data -> {
                    observer.endStep();
                    return true;
                },
                clingo_ground_program_observer.end_step.descriptor(), arena, policy));

        clingo_ground_program_observer.rule(struct, Upcalls.stub(
                clingo_ground_program_observer.rule.Function.class,
                (choice, head, headSize, body, bodySize, data) -> {
                    observer.rule(choice, NativeArrays.toInts(head, headSize), NativeArrays.toInts(body, bodySize));
                    return true;
                },
                clingo_ground_program_observer.rule.descriptor(), arena, policy));

        clingo_ground_program_observer.weight_rule(struct, Upcalls.stub(
                clingo_ground_program_observer.weight_rule.Function.class,
                (choice, head, headSize, lowerBound, body, bodySize, data) -> {
                    observer.weightRule(choice, NativeArrays.toInts(head, headSize), lowerBound,
                            WeightedLiteral.fromNative(body, (int) bodySize));
                    return true;
                },
                clingo_ground_program_observer.weight_rule.descriptor(), arena, policy));

        clingo_ground_program_observer.minimize(struct, Upcalls.stub(
                clingo_ground_program_observer.minimize.Function.class,
                (priority, literals, size, data) -> {
                    observer.minimize(priority, WeightedLiteral.fromNative(literals, (int) size));
                    return true;
                },
                clingo_ground_program_observer.minimize.descriptor(), arena, policy));

        clingo_ground_program_observer.project(struct, Upcalls.stub(
                clingo_ground_program_observer.project.Function.class,
                (atoms, size, data) -> {
                    observer.project(NativeArrays.toInts(atoms, size));
                    return true;
                },
                clingo_ground_program_observer.project.descriptor(), arena, policy));

        clingo_ground_program_observer.output_atom(struct, Upcalls.stub(
                clingo_ground_program_observer.output_atom.Function.class,
                (symbol, atom, data) -> {
                    observer.outputAtom(Symbol.fromLong(symbol), atom);
                    return true;
                },
                clingo_ground_program_observer.output_atom.descriptor(), arena, policy));

        clingo_ground_program_observer.output_term(struct, Upcalls.stub(
                clingo_ground_program_observer.output_term.Function.class,
                (symbol, condition, size, data) -> {
                    observer.outputTerm(Symbol.fromLong(symbol), NativeArrays.toInts(condition, size));
                    return true;
                },
                clingo_ground_program_observer.output_term.descriptor(), arena, policy));

        clingo_ground_program_observer.external(struct, Upcalls.stub(
                clingo_ground_program_observer.external.Function.class,
                (atom, type, data) -> {
                    observer.external(atom, ExternalType.fromValue(type));
                    return true;
                },
                clingo_ground_program_observer.external.descriptor(), arena, policy));

        clingo_ground_program_observer.assume(struct, Upcalls.stub(
                clingo_ground_program_observer.assume.Function.class,
                (literals, size, data) -> {
                    observer.assume(NativeArrays.toInts(literals, size));
                    return true;
                },
                clingo_ground_program_observer.assume.descriptor(), arena, policy));

        clingo_ground_program_observer.heuristic(struct, Upcalls.stub(
                clingo_ground_program_observer.heuristic.Function.class,
                (atom, type, bias, priority, condition, size, data) -> {
                    observer.heuristic(atom, HeuristicType.fromValue(type), bias, priority,
                            NativeArrays.toInts(condition, size));
                    return true;
                },
                clingo_ground_program_observer.heuristic.descriptor(), arena, policy));

        clingo_ground_program_observer.acyc_edge(struct, Upcalls.stub(
                clingo_ground_program_observer.acyc_edge.Function.class,
                (nodeU, nodeV, condition, size, data) -> {
                    observer.acycEdge(nodeU, nodeV, NativeArrays.toInts(condition, size));
                    return true;
                },
                clingo_ground_program_observer.acyc_edge.descriptor(), arena, policy));

        clingo_ground_program_observer.theory_term_number(struct, Upcalls.stub(
                clingo_ground_program_observer.theory_term_number.Function.class,
                (termId, number, data) -> {
                    observer.theoryTermNumber(termId, number);
                    return true;
                },
                clingo_ground_program_observer.theory_term_number.descriptor(), arena, policy));

        clingo_ground_program_observer.theory_term_string(struct, Upcalls.stub(
                clingo_ground_program_observer.theory_term_string.Function.class,
                (termId, name, data) -> {
                    observer.theoryTermString(termId, NativeStr.toJava(name));
                    return true;
                },
                clingo_ground_program_observer.theory_term_string.descriptor(), arena, policy));

        clingo_ground_program_observer.theory_term_compound(struct, Upcalls.stub(
                clingo_ground_program_observer.theory_term_compound.Function.class,
                (termId, nameIdOrType, arguments, size, data) -> {
                    observer.theoryTermCompound(termId, nameIdOrType, NativeArrays.toInts(arguments, size));
                    return true;
                },
                clingo_ground_program_observer.theory_term_compound.descriptor(), arena, policy));

        clingo_ground_program_observer.theory_element(struct, Upcalls.stub(
                clingo_ground_program_observer.theory_element.Function.class,
                (elementId, terms, termsSize, condition, conditionSize, data) -> {
                    observer.theoryElement(elementId, NativeArrays.toInts(terms, termsSize),
                            NativeArrays.toInts(condition, conditionSize));
                    return true;
                },
                clingo_ground_program_observer.theory_element.descriptor(), arena, policy));

        clingo_ground_program_observer.theory_atom(struct, Upcalls.stub(
                clingo_ground_program_observer.theory_atom.Function.class,
                (atomIdOrZero, termId, elements, size, data) -> {
                    observer.theoryAtom(atomIdOrZero, termId, NativeArrays.toInts(elements, size));
                    return true;
                },
                clingo_ground_program_observer.theory_atom.descriptor(), arena, policy));

        clingo_ground_program_observer.theory_atom_with_guard(struct, Upcalls.stub(
                clingo_ground_program_observer.theory_atom_with_guard.Function.class,
                (atomIdOrZero, termId, elements, size, operatorId, rightHandSideId, data) -> {
                    observer.theoryAtomWithGuard(atomIdOrZero, termId, NativeArrays.toInts(elements, size),
                            operatorId, rightHandSideId);
                    return true;
                },
                clingo_ground_program_observer.theory_atom_with_guard.descriptor(), arena, policy));

        return struct;
    }
}
