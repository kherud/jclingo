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

package org.potassco.clingo.internal;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.potassco.clingo.control.LoggerCallback;
import org.potassco.clingo.control.WarningCode;
import org.potassco.clingo.internal.ffi.clingo_logger_t;

/**
 * Upcall stubs for callbacks that are used by more than one package.
 */
public final class Callbacks {

    private Callbacks() {
    }

    /**
     * @param logger the logger to bridge, may be {@code null}
     * @param arena  the arena the stub lives in, which has to outlive clingo's use of the logger
     * @param policy what to do when the logger throws
     * @return a {@code clingo_logger_t}, or {@link MemorySegment#NULL} if there is no logger
     */
    public static MemorySegment logger(LoggerCallback logger, Arena arena, ErrorPolicy policy) {
        if (logger == null) {
            return MemorySegment.NULL;
        }
        clingo_logger_t.Function bridge = (code, message, data) ->
                logger.call(WarningCode.fromValue(code), NativeStr.toJava(message));
        return Upcalls.stub(clingo_logger_t.Function.class, bridge, clingo_logger_t.descriptor(), arena, policy);
    }
}
