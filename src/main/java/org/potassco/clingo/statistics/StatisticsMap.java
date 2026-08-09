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

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

public class StatisticsMap extends Statistics {

    public StatisticsMap(MemorySegment statistics, long key) {
        super(statistics, key);
    }

    /**
     * Get the number of subkeys of a map entry.
     *
     * @return the resulting number
     */
    @Override
    public int size() {
        return (int) Out.getLong(out -> ClingoH.clingo_statistics_map_size(statistics, key, out));
    }

    /**
     * Get the value at the given offset of a map entry.
     *
     * @param name the key of the entry
     * @return the value of the key
     */
    @Override
    public Statistics get(String name) {
        long subkey = getSubKey(name);
        return Statistics.fromKey(statistics, subkey);
    }

    /**
     * Get the name associated with the offset-th subkey.
     *
     * @param index the offset of the name
     * @return the resulting name
     */
    public String getKey(int index) {
        return Out.getString(out -> ClingoH.clingo_statistics_map_subkey_name(statistics, key, index, out));
    }

    /**
     * Get the names of all subkeys.
     *
     * @return the resulting keys
     */
    public String[] keys() {
        int size = size();
        String[] keys = new String[size];
        for (int i = 0; i < size; i++)
            keys[i] = getKey(i);
        return keys;
    }

    /**
     * Test if the given map contains a specific subkey.
     *
     * @param name the name of the key
     * @return true if the map has a subkey with the given name
     */
    public boolean hasKey(String name) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            return Out.getBoolean(out -> ClingoH.clingo_statistics_map_has_subkey(statistics, key, name$, out));
        }
    }

    /**
     * Add a subkey with the given name.
     *
     * @param name the name of the new subkey
     * @param type the type of the new subkey
     * @return the resulting entry
     */
    public Statistics addKey(String name, StatisticsType type) {
        return fromKey(statistics, addSubKey(name, type), type);
    }

    /**
     * Set a value of the map for the specified key
     *
     * @param name  the key name
     * @param value the raw value
     * @return the newly created entry
     */
    public StatisticsValue set(String name, double value) {
        return new StatisticsValue(statistics, addSubKey(name, StatisticsType.VALUE), value);
    }

    /**
     * Set an array of values for the specified map key
     *
     * @param name   the key name
     * @param values the raw values
     * @return the newly created entry
     */
    public StatisticsArray set(String name, double[] values) {
        return new StatisticsArray(statistics, addSubKey(name, StatisticsType.ARRAY), values);
    }

    /**
     * Set a statistics object for the specified map key
     *
     * @param name  the key name
     * @param entry the statistics object
     * @return the newly created entry
     */
    public Statistics set(String name, Statistics entry) {
        Statistics subKey = addKey(name, entry.getType());
        subKey.copyFrom(entry);
        return subKey;
    }

    /**
     * Lookup a subkey under the given name.
     * <p>
     * Multiple levels can be looked up by concatenating keys with a period.
     *
     * @param name the name to lookup the subkey
     * @return the resulting subkey
     */
    private long getSubKey(String name) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            return Out.getLong(out -> ClingoH.clingo_statistics_map_at(statistics, key, name$, out));
        }
    }

    private long addSubKey(String name, StatisticsType type) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            return Out.getLong(out ->
                    ClingoH.clingo_statistics_map_add_subkey(statistics, key, name$, type.getValue(), out));
        }
    }

    /**
     * @return the type of this statistics object
     */
    @Override
    public StatisticsType getType() {
        return StatisticsType.MAP;
    }

    @Override
    public void copyFrom(Statistics source) {
        super.copyFrom(source);
        int size = source.size();
        for (int i = 0; i < size; i++) {
            String name = ((StatisticsMap) source).getKey(i);
            Statistics element = source.get(name);
            addKey(name, element.getType()).copyFrom(element);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        int size = size();
        for (int i = 0; i < size; i++) {
            String name = getKey(i);
            builder.append('"').append(name).append('"');
            builder.append(": ");
            builder.append(get(name).toString());
            if (i < size - 1)
                builder.append(", ");
        }
        builder.append("}");
        return builder.toString();
    }
}
