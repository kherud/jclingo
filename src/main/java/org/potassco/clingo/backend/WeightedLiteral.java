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

import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

/**
 * A Literal with an associated weight.
 */
public record WeightedLiteral(int literal, int weight) {

    /**
     * {@code clingo_weighted_literal_t} is two ints, so an array of them is just an int array of twice the length.
     *
     * @return the given literals in the contiguous memory clingo expects
     */
    public static MemorySegment toNative(SegmentAllocator allocator, WeightedLiteral[] literals) {
        int[] values = new int[literals.length * 2];
        for (int i = 0; i < literals.length; i++) {
            values[2 * i] = literals[i].literal;
            values[2 * i + 1] = literals[i].weight;
        }
        return allocator.allocateFrom(ValueLayout.JAVA_INT, values);
    }

    /**
     * @param literals a {@code clingo_weighted_literal_t} array
     * @param size     the number of literals in it
     */
    public static WeightedLiteral[] fromNative(MemorySegment literals, int size) {
        int[] values = literals.reinterpret(size * 2L * Integer.BYTES).toArray(ValueLayout.JAVA_INT);
        WeightedLiteral[] result = new WeightedLiteral[size];
        for (int i = 0; i < size; i++) {
            result[i] = new WeightedLiteral(values[2 * i], values[2 * i + 1]);
        }
        return result;
    }
}
