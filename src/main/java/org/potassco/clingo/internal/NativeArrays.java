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

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Bulk copies between clingo's arrays and Java arrays.
 */
public final class NativeArrays {

    private static final int[] NO_INTS = new int[0];
    private static final long[] NO_LONGS = new long[0];

    private NativeArrays() {
    }

    /**
     * @param array a pointer clingo passed to a callback, which is only valid during that call
     * @param size  the number of elements in it
     */
    public static int[] toInts(MemorySegment array, long size) {
        return size == 0 ? NO_INTS : array.reinterpret(size * Integer.BYTES).toArray(ValueLayout.JAVA_INT);
    }

    /**
     * @param array a pointer clingo passed to a callback, which is only valid during that call
     * @param size  the number of elements in it
     */
    public static long[] toLongs(MemorySegment array, long size) {
        return size == 0 ? NO_LONGS : array.reinterpret(size * Long.BYTES).toArray(ValueLayout.JAVA_LONG);
    }
}
