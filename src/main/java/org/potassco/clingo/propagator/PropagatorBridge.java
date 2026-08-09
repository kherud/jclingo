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

package org.potassco.clingo.propagator;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.NativeArrays;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.clingo_propagator;

/**
 * Builds the {@code clingo_propagator_t} clingo expects from a {@link Propagator}.
 */
public final class PropagatorBridge {

    private PropagatorBridge() {
    }

    /**
     * @param arena  the arena owning the struct and its stubs; solver threads call them until the control is freed
     * @param policy what to do when a propagator method throws
     * @return a {@code clingo_propagator_t}
     */
    public static MemorySegment of(Propagator propagator, Arena arena, ErrorPolicy policy) {
        MemorySegment struct = clingo_propagator.allocate(arena);

        clingo_propagator.init(struct, Upcalls.stub(
                clingo_propagator.init.Function.class,
                (init, data) -> {
                    propagator.init(new PropagateInit(init));
                    return true;
                },
                clingo_propagator.init.descriptor(), arena, policy));

        clingo_propagator.propagate(struct, Upcalls.stub(
                clingo_propagator.propagate.Function.class,
                (control, changes, size, data) -> {
                    propagator.propagate(new PropagateControl(control), NativeArrays.toInts(changes, size));
                    return true;
                },
                clingo_propagator.propagate.descriptor(), arena, policy));

        clingo_propagator.undo(struct, Upcalls.stub(
                clingo_propagator.undo.Function.class,
                (control, changes, size, data) ->
                        propagator.undo(new PropagateControl(control), NativeArrays.toInts(changes, size)),
                clingo_propagator.undo.descriptor(), arena, policy));

        clingo_propagator.check(struct, Upcalls.stub(
                clingo_propagator.check.Function.class,
                (control, data) -> {
                    propagator.check(new PropagateControl(control));
                    return true;
                },
                clingo_propagator.check.descriptor(), arena, policy));

        clingo_propagator.decide(struct, Upcalls.stub(
                clingo_propagator.decide.Function.class,
                (threadId, assignment, fallback, data, decision) -> {
                    int literal = propagator.decide(threadId, new Assignment(assignment), fallback);
                    decision.reinterpret(Integer.BYTES).set(ValueLayout.JAVA_INT, 0, literal);
                    return true;
                },
                clingo_propagator.decide.descriptor(), arena, policy));

        return struct;
    }
}
