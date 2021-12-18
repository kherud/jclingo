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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.internal.Clingo;

import java.util.NoSuchElementException;

import static org.potassco.clingo.statistics.StatisticsType.fromValue;

/**
 * Object to capture clingo's native statistics
 */
public abstract class Statistics {

    protected final Pointer statistics;
    protected final long key;

    public Statistics(Pointer statistics, long key) {
        this.statistics = statistics;
        this.key = key;
    }

    /**
     * Get the raw value of an statistics entry.
     * Throws {@link IllegalStateException} if this entry is not a value.
     *
     * @return the raw value
     */
    public double get() {
        throw new IllegalStateException("Statistics entry is not a value");
    }

    /**
     * Get the value at the given offset of a map entry.
     * Multiple levels can be looked up by concatenating keys with a period.
     * Throws {@link IllegalStateException} if this entry is not a map.
     *
     * @param name the key of the entry
     * @return the value of the key
     */
    public Statistics get(String name) {
        throw new IllegalStateException("Statistics entry is not a map");
    }

    /**
     * Get the value at the given index of an array entry.
     * Throws {@link IllegalStateException} if this entry is not an array.
     *
     * @param index the index of the entry
     * @return the value at the index
     */
    public Statistics get(int index) {
        throw new IllegalStateException("Statistics entry is not an array");
    }

    /**
     * Returns the statistics root entry given a pointer to an existing native object
     *
     * @param statistics the native pointer to the statistics object
     * @return the java statistics object
     */
    public static Statistics fromPointer(Pointer statistics) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_root(statistics, longByReference));
        return fromKey(statistics, longByReference.getValue());
    }

    /**
     * Returns a statistics entry corresponding to the key
     *
     * @param statistics a native pointer to the statistics object (see {@link Control#getStatistics}
     * @param key the key of the object
     * @return the statistics entry
     */
    public static Statistics fromKey(Pointer statistics, long key) {
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_type(statistics, key, intByReference));
        StatisticsType type = fromValue(intByReference.getValue());
        return fromKey(statistics, key, type);
    }

    /**
     * Returns a statistics entry of the specified type corresponding to the key
     *
     * @param statistics a native pointer to the statistics object (see {@link Control#getStatistics}
     * @param key the key of the object
     * @param type the known type of the statistics entry
     * @return the statistics entry
     */
    public static Statistics fromKey(Pointer statistics, long key, StatisticsType type) {
        switch (type) {
            case VALUE: return new StatisticsValue(statistics, key);
            case ARRAY: return new StatisticsArray(statistics, key);
            case MAP: return new StatisticsMap(statistics, key);
            case EMPTY: throw new NoSuchElementException("Unknown statistics entry with key '" + key + "'");
            default: throw new IllegalStateException("Unknown statistics type ordinal '" + type.name() + "'");
        }
    }

    /**
     * @return the type of this statistics object
     */
    public abstract StatisticsType getType();

}
