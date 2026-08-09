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

import org.potassco.clingo.internal.ErrorPolicy;
import org.potassco.clingo.internal.Upcalls;
import org.potassco.clingo.internal.ffi.clingo_ground_callback_t;

/**
 * Builds the {@code clingo_ground_callback_t} clingo expects from a {@link GroundCallback}.
 */
public final class GroundCallbackBridge {

    private GroundCallbackBridge() {
    }

    /**
     * @param callback the callback to bridge, may be {@code null}
     * @param arena    the arena the stub lives in; the callback only runs during the grounding call
     * @return a {@code clingo_ground_callback_t}, or {@link MemorySegment#NULL} if there is no callback
     */
    public static MemorySegment of(GroundCallback callback, Arena arena, ErrorPolicy policy) {
        if (callback == null) {
            return MemorySegment.NULL;
        }
        clingo_ground_callback_t.Function bridge =
                (location, name, arguments, size, data, symbolCallback, symbolData) -> {
                    callback.ground(location, name, arguments, size, symbolCallback, symbolData);
                    return true;
                };
        return Upcalls.stub(clingo_ground_callback_t.Function.class, bridge, clingo_ground_callback_t.descriptor(),
                arena, policy);
    }
}
