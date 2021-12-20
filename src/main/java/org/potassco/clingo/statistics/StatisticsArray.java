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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

public class StatisticsArray extends Statistics {

    public StatisticsArray(Pointer statistics, long key) {
        super(statistics, key);
    }

    public StatisticsArray(Pointer statistics, long key, double[] values) {
        super(statistics, key);
        add(values);
    }

    /**
     * Get the size of an array entry.
     *
     * @return the resulting size
     */
    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_array_size(statistics, key, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
    }


    /**
     * Get the value at the given index of an array entry.
     *
     * @param index the index of the entry
     * @return the value at the index
     */
    @Override
    public Statistics get(int index) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_array_at(statistics, key, new NativeSize(index), longByReference));
        return fromKey(statistics, longByReference.getValue());
    }

    /**
     * Create the subkey at the end of an array entry.
     *
     * @param type the type of the new subkey
     * @return the resulting statistics object
     */
    public Statistics add(StatisticsType type) {
        LongByReference longByReference = new LongByReference();
        Clingo.INSTANCE.clingo_statistics_array_push(statistics, key, type.getValue(), longByReference);
        return Statistics.fromKey(statistics, longByReference.getValue(), type);
    }

    /**
     * Adds an array of raw values to this entry
     *
     * @param values the raw values
     */
    public void add(double[] values) {
        LongByReference longByReference = new LongByReference();
        for (double value : values) {
            Clingo.check(Clingo.INSTANCE.clingo_statistics_array_push(statistics, key, StatisticsType.VALUE.getValue(), longByReference));
            Clingo.check(Clingo.INSTANCE.clingo_statistics_value_set(statistics, longByReference.getValue(), value));
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
