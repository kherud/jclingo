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

package org.potassco.clingo.statistics;

import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

public class StatisticsArray extends Statistics {

    public StatisticsArray(MemorySegment statistics, long key) {
        super(statistics, key);
    }

    public StatisticsArray(MemorySegment statistics, long key, double[] values) {
        super(statistics, key);
        add(values);
    }

    /**
     * Get the size of an array entry.
     *
     * @return the resulting size
     */
    @Override
    public int size() {
        return (int) Out.getLong(out -> ClingoH.clingo_statistics_array_size(statistics, key, out));
    }


    /**
     * Get the value at the given index of an array entry.
     *
     * @param index the index of the entry
     * @return the value at the index
     */
    @Override
    public Statistics get(int index) {
        return fromKey(statistics, Out.getLong(out -> ClingoH.clingo_statistics_array_at(statistics, key, index, out)));
    }

    /**
     * Create the subkey at the end of an array entry.
     *
     * @param type the type of the new subkey
     * @return the resulting statistics object
     */
    public Statistics add(StatisticsType type) {
        return Statistics.fromKey(statistics,
                Out.getLong(out -> ClingoH.clingo_statistics_array_push(statistics, key, type.getValue(), out)), type);
    }

    /**
     * Adds an array of raw values to this entry
     *
     * @param values the raw values
     */
    public void add(double[] values) {
        for (double value : values) {
            long subKey = Out.getLong(out ->
                    ClingoH.clingo_statistics_array_push(statistics, key, StatisticsType.VALUE.getValue(), out));
            Clingo.check(ClingoH.clingo_statistics_value_set(statistics, subKey, value));
        }
    }

    /**
     * @return the type of this statistics object
     */
    @Override
    public StatisticsType getType() {
        return StatisticsType.ARRAY;
    }

    @Override
    public void copyFrom(Statistics source) {
        super.copyFrom(source);
        int size = source.size();
        for (int i = 0; i < size; i++) {
            Statistics element = source.get(i);
            add(element.getType()).copyFrom(element);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int size = size();
        for (int i = 0; i < size; i++) {
            builder.append(get(i).toString());
            if (i < size - 1)
                builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }
}
