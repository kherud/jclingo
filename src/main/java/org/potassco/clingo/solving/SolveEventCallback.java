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
import java.lang.foreign.ValueLayout;
import java.util.concurrent.atomic.AtomicReference;

import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.NativeArrays;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.clingo_solve_event_callback_t;
import org.potassco.clingo.statistics.Statistics;

/**
 * Callback function called during search to notify when the search is finished or a model is ready.
 * <p>
 * An exception thrown by one of the handlers stops the search and is rethrown by the {@link SolveHandle}. Calling
 * {@link #stop()} stops the search without an error.
 * <p>
 * You have to inherit from this class to implement your own callback.
 */
public abstract class SolveEventCallback {

    private volatile boolean stopped;

    /**
     * Stops the search. Can be called from any of the handlers below, but only takes effect for models and lower
     * bounds, because clingo ignores the request once the search is finished.
     */
    protected void stop() {
        stopped = true;
    }

    public void onModel(Model model) {

    }

    public void onUnsat(long[] cost) {

    }

    public void onStatistics(Statistics perStep, Statistics accumulated) {

    }

    public void onResult(SolveResult solveResult) {

    }

    /**
     * @param callback the callback to bridge, may be {@code null}
     * @param arena    the arena the stub lives in; the solve handle closes it when the search is over
     * @param errors   where an error of this callback is recorded, since reporting it to clingo would exit the process
     * @return a {@code clingo_solve_event_callback_t}, or {@link MemorySegment#NULL} if there is no callback
     */
    public static MemorySegment toNative(SolveEventCallback callback, Arena arena, AtomicReference<Throwable> errors) {
        if (callback == null) {
            return MemorySegment.NULL;
        }
        clingo_solve_event_callback_t.Function bridge = (type, event, data, goon) -> {
            callback.dispatch(type, event);
            if (callback.stopped) {
                goon.reinterpret(1).set(ValueLayout.JAVA_BOOLEAN, 0, false);
            }
            return true;
        };
        return Upcalls.stub(clingo_solve_event_callback_t.Function.class, bridge,
                clingo_solve_event_callback_t.descriptor(), arena, ErrorPolicy.solveEvent(errors));
    }

    private void dispatch(int code, MemorySegment event) {
        SolveEventType type = SolveEventType.fromValue(code);
        switch (type) {
            case MODEL -> onModel(new Model(event));
            case UNSAT -> {
                // struct { int64_t const *cost; size_t size; }
                MemorySegment unsat = event.reinterpret(ValueLayout.ADDRESS.byteSize() + Long.BYTES);
                MemorySegment cost = unsat.get(ValueLayout.ADDRESS, 0);
                long size = unsat.get(ValueLayout.JAVA_LONG, ValueLayout.ADDRESS.byteSize());
                onUnsat(NativeArrays.toLongs(cost, size));
            }
            case STATISTICS -> {
                // clingo_statistics_t *[2], the per step and the accumulated statistics
                MemorySegment statistics = event.reinterpret(2 * ValueLayout.ADDRESS.byteSize());
                onStatistics(Statistics.fromPointer(statistics.getAtIndex(ValueLayout.ADDRESS, 0)),
                        Statistics.fromPointer(statistics.getAtIndex(ValueLayout.ADDRESS, 1)));
            }
            case FINISH -> onResult(new SolveResult(event.reinterpret(Integer.BYTES).get(ValueLayout.JAVA_INT, 0)));
            default -> throw new IllegalStateException("unknown solve event type " + type);
        }
    }
}
