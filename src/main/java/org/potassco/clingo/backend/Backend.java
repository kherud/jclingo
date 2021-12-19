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
import org.potassco.clingo.symbol.Symbol;

/**
 * Backend object providing a low level interface to extend a logic program.
 * This class allows for adding statements in ASPIF format.
 * The `Backend` is a context manager and should be used with Java's `try with` statement.
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
        // TODO: what are acyc edges?!
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
     * Can also be used to release an external atom using `TruthValue.Release`.
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
     * Add a disjuntive or choice rule with one weight constraint with a lower bound in the body to the program.
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

    @Override
    public void close() {
        Clingo.check(Clingo.INSTANCE.clingo_backend_end(backend));
    }
}
