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
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

/**
 * Conversions between Java strings and the zero terminated UTF-8 strings clingo consumes and produces.
 */
public final class NativeStr {

    /**
     * The encoding clingo uses for all strings it consumes and produces.
     */
    public static final String STRING_ENCODING = "UTF-8";

    private NativeStr() {
    }

    /**
     * @return a zero terminated copy of {@code value}, or {@link MemorySegment#NULL} for {@code null}
     */
    public static MemorySegment of(SegmentAllocator allocator, String value) {
        return value == null ? MemorySegment.NULL : allocator.allocateFrom(value);
    }

    /**
     * @return the string {@code segment} points at, or {@code null} for a null pointer
     */
    public static String toJava(MemorySegment segment) {
        if (segment == null || segment.equals(MemorySegment.NULL)) {
            return null;
        }
        return (segment.byteSize() == 0 ? segment.reinterpret(Long.MAX_VALUE) : segment).getString(0);
    }

    /**
     * Reads a string clingo hands out through its usual two step protocol: ask for the size, then fill a buffer.
     *
     * @param size the call writing the required buffer size, including the zero terminator
     * @param fill the call writing the string into the buffer
     */
    public static String getSized(SizeCall size, FillCall fill) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment sizeOut = arena.allocate(ValueLayout.JAVA_LONG);
            Clingo.check(size.call(sizeOut));
            long length = sizeOut.get(ValueLayout.JAVA_LONG, 0);
            MemorySegment buffer = arena.allocate(length);
            Clingo.check(fill.call(buffer, length));
            return buffer.getString(0);
        }
    }

    /**
     * A clingo call writing a required buffer size into its out parameter.
     */
    @FunctionalInterface
    public interface SizeCall {
        boolean call(MemorySegment size);
    }

    /**
     * A clingo call writing a string into a buffer of the previously requested size.
     */
    @FunctionalInterface
    public interface FillCall {
        boolean call(MemorySegment buffer, long size);
    }
}
