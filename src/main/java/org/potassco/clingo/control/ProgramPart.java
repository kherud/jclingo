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

package org.potassco.clingo.control;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.ffi.clingo_part;
import org.potassco.clingo.symbol.Symbol;

/**
 * A program part to be grounded, i.e. a {@code #program} name together with the arguments it is instantiated with.
 */
public class ProgramPart {

    private final String name;
    private final Symbol[] parameters;

    public ProgramPart(String name, Symbol... parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public Symbol[] getParameters() {
        return parameters.clone();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(name).append('(');
        for (int i = 0; i < parameters.length; i++) {
            builder.append(i == 0 ? "" : ",").append(parameters[i]);
        }
        return builder.append(')').toString();
    }

    /**
     * @return the given parts as the contiguous {@code clingo_part_t} array clingo expects
     */
    static MemorySegment toNative(SegmentAllocator allocator, ProgramPart[] parts) {
        MemorySegment array = clingo_part.allocateArray(parts.length, allocator);
        for (int i = 0; i < parts.length; i++) {
            ProgramPart part = parts[i];
            MemorySegment element = clingo_part.asSlice(array, i);
            clingo_part.name(element, NativeStr.of(allocator, part.name));
            clingo_part.size(element, part.parameters.length);
            long[] symbols = new long[part.parameters.length];
            for (int j = 0; j < symbols.length; j++) {
                symbols[j] = part.parameters[j].getLong();
            }
            clingo_part.params(element, symbols.length == 0
                    ? MemorySegment.NULL
                    : allocator.allocateFrom(ValueLayout.JAVA_LONG, symbols));
        }
        return array;
    }
}
