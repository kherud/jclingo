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
import java.lang.foreign.ValueLayout;

/**
 * Runs a clingo call that reports its result through an out parameter, in an arena that lasts exactly as long as the
 * call does.
 */
public final class Out {

    private Out() {
    }

    public static boolean getBoolean(Call call) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment out = arena.allocate(ValueLayout.JAVA_BOOLEAN);
            Clingo.check(call.call(out));
            return out.get(ValueLayout.JAVA_BOOLEAN, 0);
        }
    }

    public static byte getByte(Call call) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment out = arena.allocate(ValueLayout.JAVA_BYTE);
            Clingo.check(call.call(out));
            return out.get(ValueLayout.JAVA_BYTE, 0);
        }
    }

    public static int getInt(Call call) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment out = arena.allocate(ValueLayout.JAVA_INT);
            Clingo.check(call.call(out));
            return out.get(ValueLayout.JAVA_INT, 0);
        }
    }

    public static long getLong(Call call) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment out = arena.allocate(ValueLayout.JAVA_LONG);
            Clingo.check(call.call(out));
            return out.get(ValueLayout.JAVA_LONG, 0);
        }
    }

    public static double getDouble(Call call) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment out = arena.allocate(ValueLayout.JAVA_DOUBLE);
            Clingo.check(call.call(out));
            return out.get(ValueLayout.JAVA_DOUBLE, 0);
        }
    }

    /**
     * @return the pointer written into the out parameter; it stays owned by clingo
     */
    public static MemorySegment getPointer(Call call) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment out = arena.allocate(ValueLayout.ADDRESS);
            Clingo.check(call.call(out));
            return out.get(ValueLayout.ADDRESS, 0);
        }
    }

    /**
     * @return the string the out parameter points at; clingo internalizes such strings
     */
    public static String getString(Call call) {
        return NativeStr.toJava(getPointer(call));
    }

    /**
     * @return a copy of the int array clingo reports through a pointer and a size out parameter
     */
    public static int[] getInts(ArrayCall call) {
        try (Arena arena = Arena.ofConfined()) {
            return getArray(arena, call, Integer.BYTES).toArray(ValueLayout.JAVA_INT);
        }
    }

    /**
     * @return a copy of the long array clingo reports through a pointer and a size out parameter
     */
    public static long[] getLongs(ArrayCall call) {
        try (Arena arena = Arena.ofConfined()) {
            return getArray(arena, call, Long.BYTES).toArray(ValueLayout.JAVA_LONG);
        }
    }

    private static MemorySegment getArray(Arena arena, ArrayCall call, int elementSize) {
        MemorySegment pointer = arena.allocate(ValueLayout.ADDRESS);
        MemorySegment size = arena.allocate(ValueLayout.JAVA_LONG);
        Clingo.check(call.call(pointer, size));
        long count = size.get(ValueLayout.JAVA_LONG, 0);
        return count == 0 ? MemorySegment.NULL.reinterpret(0)
                : pointer.get(ValueLayout.ADDRESS, 0).reinterpret(count * elementSize);
    }

    /**
     * A clingo call writing its result into an out parameter.
     */
    @FunctionalInterface
    public interface Call {
        boolean call(MemorySegment out);
    }

    /**
     * A clingo call reporting an array through a pointer and a size out parameter.
     */
    @FunctionalInterface
    public interface ArrayCall {
        boolean call(MemorySegment pointer, MemorySegment size);
    }
}
