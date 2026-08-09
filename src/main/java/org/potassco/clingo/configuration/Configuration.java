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

package org.potassco.clingo.configuration;

import java.util.ArrayList;
import java.util.List;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

import org.potassco.clingo.configuration.args.Option;
import org.potassco.clingo.configuration.args.Statistics;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeStr;
import org.potassco.clingo.internal.Out;
import org.potassco.clingo.internal.ffi.ClingoH;

import static org.potassco.clingo.configuration.ConfigurationType.Type.*;

/**
 * Allows for changing the configuration of the underlying solver.
 * <p>
 * Options are organized hierarchically. To change and inspect an option use:
 * <pre>
 * config.group.subgroup.option = "value"
 * value = config.group.subgroup.option
 * </pre>
 * There are also arrays of option groups that can be accessed using integer
 * indices:
 * <pre>
 * config.group.subgroup[0].option = "value1"
 * config.group.subgroup[1].option = "value2"
 * </pre>
 * To list the subgroups of an option group, use {@link #getKeys()}.
 * Array option groups, like solver, can be iterated. Furthermore,
 * there are meta options having key <code>configuration</code>. Assigning a meta option
 * sets a number of related options.  To get further information about an
 * option or option group, use {@link #getDescription(String)}.
 * <p>
 * The value of an option is always a string and any value assigned to an
 * option is automatically converted into a string.
 */
public class Configuration {

    private final MemorySegment configuration;
    private final int key;
    private ConfigurationType typeBitset;

    public Configuration(Control control) {
        this.configuration = Out.getPointer(out ->
                ClingoH.clingo_control_configuration(control.getPointer(), out));
        this.key = Out.getInt(out -> ClingoH.clingo_configuration_root(configuration, out));
    }

    public Configuration(MemorySegment configuration, int key) {
        this.configuration = configuration;
        this.key = key;
    }

    /**
     * Get the types of a key.
     * The type is bitset, an entry can have multiple (but at least one) type.
     *
     * @return the resulting type
     */
    public ConfigurationType getType() {
        if (typeBitset == null) {
            typeBitset = new ConfigurationType(
                    Out.getInt(out -> ClingoH.clingo_configuration_type(configuration, key, out)));
        }
        return typeBitset;
    }

    /**
     * Return whether the configuration object is of a specific type.
     * The type is bitset, an entry can have multiple (but at least one) type.
     *
     * @param type the type to check
     * @return a boolean if the configuration is of this type
     */
    public boolean isType(ConfigurationType.Type type) {
        ConfigurationType bitset = getType();
        return (bitset.getBitset() & type.getInt()) > 0;
    }

    /**
     * Get the size of an array entry.
     * The type of the entry must be {@link ConfigurationType.Type#ARRAY}.
     *
     * @return the size of the array
     */
    public int arraySize() {
        if (!isArray())
            throw new IllegalStateException("configuration entry is not an array");
        return (int) Out.getLong(out -> ClingoH.clingo_configuration_array_size(configuration, key, out));
    }

    /**
     * Get the size of a map entry.
     * The type of the entry must be {@link ConfigurationType.Type#MAP}.
     *
     * @return the size of the map
     */
    public int mapSize() {
        if (!isMap())
            throw new IllegalStateException("configuration entry is not a map");
        return (int) Out.getLong(out -> ClingoH.clingo_configuration_map_size(configuration, key, out));
    }

    /**
     * Get a description for an option or option group.
     *
     * @param name The name of the option.
     * @return the text description
     */
    public String getDescription(String name) {
        if (!isMap())
            throw new IllegalStateException("configuration entry is not a map");

        int subkey = getSubkey(name);
        return Out.getString(out -> ClingoH.clingo_configuration_description(configuration, subkey, out));
    }

    private int getSubkey(String name) {
        if (!isMap())
            throw new IllegalStateException("configuration entry is not a map");
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            if (!Out.getBoolean(out -> ClingoH.clingo_configuration_map_has_subkey(configuration, key, name$, out))) {
                throw new IllegalStateException("configuration entry does not have option '" + name + "'");
            }
            return Out.getInt(out -> ClingoH.clingo_configuration_map_at(configuration, key, name$, out));
        }
    }

    /**
     * The list of names of sub-option groups or options.
     * Throws an error if the current object is not an option group.
     *
     * @return the list of keys
     */
    public List<String> getKeys() {
        if (!isMap())
            throw new IllegalStateException("configuration entry is not a map");

        int mapSize = mapSize();
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < mapSize; i++) {
            keys.add(getKey(i));
        }
        return keys;
    }

    /**
     * Get the name associated with the offset-th subkey.
     * Throws an error if the current object is not an option group.
     *
     * @return the key
     */
    public String getKey(int index) {
        if (!isMap())
            throw new IllegalStateException("configuration entry is not a map");
        return Out.getString(out -> ClingoH.clingo_configuration_map_subkey_name(configuration, key, index, out));
    }

    /**
     * Check whether an entry has a value.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     *
     * @return whether the entry has a value
     */
    public boolean isAssigned() {
        return Out.getBoolean(out -> ClingoH.clingo_configuration_value_is_assigned(configuration, key, out));
    }

    /**
     * Lookup an entry under the given name.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#MAP}.
     * Multiple levels can be looked up by concatenating keys with a period.
     *
     * @param name the name to lookup the subkey
     * @return the resulting entry
     */
    public Configuration get(String name) {
        if (!isType(MAP))
            throw new IllegalStateException("Configuration entry is not a map");
        return new Configuration(configuration, mapAt(name));
    }

    /**
     * Lookup an entry under the given index.
     * <p>
     * Some array entries, like fore example the solver configuration, can be accessed past there actual size to add subentries.
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#ARRAY}.
     *
     * @param index the offset in the array
     * @return the resulting entry
     */
    public Configuration get(int index) {
        if (!isType(ARRAY))
            throw new IllegalStateException("Configuration entry is not an array");
        return new Configuration(configuration, arrayAt(index));
    }

    /**
     * Get the string value of the given entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     *
     * @return the resulting string value
     */
    public String get() {
        if (!isType(VALUE))
            throw new IllegalStateException("Configuration entry is not a value");
        return NativeStr.getSized(
                size -> ClingoH.clingo_configuration_value_get_size(configuration, key, size),
                (buffer, size) -> ClingoH.clingo_configuration_value_get(configuration, key, buffer, size));
    }

    /**
     * Set the value of an entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     *
     * @param name  the name of the entry to change
     * @param value the value to set
     */
    public void set(String name, String value) {
        setValue(mapAt(name), value);
    }

    /**
     * Set the value of an entry.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#ARRAY}.
     *
     * @param index  the offset of the entry to change
     * @param value the value to set
     */
    public void set(int index, String value) {
        setValue(arrayAt(index), value);
    }

    private int mapAt(String name) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment name$ = arena.allocateFrom(name);
            return Out.getInt(out -> ClingoH.clingo_configuration_map_at(configuration, key, name$, out));
        }
    }

    private int arrayAt(int index) {
        return Out.getInt(out -> ClingoH.clingo_configuration_array_at(configuration, key, index, out));
    }

    private void setValue(int subkey, String value) {
        try (Arena arena = Arena.ofConfined()) {
            Clingo.check(ClingoH.clingo_configuration_value_set(configuration, subkey, arena.allocateFrom(value)));
        }
    }

    /**
     * Sets the configuration to a list of {@link Option options}
     *
     * @param options an array of options
     */
    public void set(Option... options) {
        for (Option option : options) {
            set(option);
        }
    }

    /**
     * Resets a list of options to clingo's defaults
     *
     * @param options an array of options
     */
    public void reset(Option... options) {
        for (Option option : options) {
            set(option.getDefault());
        }
    }

    /**
     * Resets an option to clingo's defaults
     *
     * @param option the option
     */
    public void reset(Option option) {
        set(option.getDefault());
    }

    /**
     * Sets clingo's native configuration to a value, with a key specified by {@link Option}
     *
     * @param option option object with the key and value
     */
    public void set(Option option) {
        String key = option.getNativeKey();
        String value = option.getValue();
        set(key, value);
    }

    /**
     * Lookup an entry under the given {@link Option option}.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#MAP}.
     *
     * @param option option object with the key
     * @return the resulting entry
     */
    public Configuration get(Option option) {
        return get(option.getNativeKey());
    }

    /**
     * Sets the level of clingo's statistics output to a value specified by {@link Statistics}
     *
     * @param level the level of output
     */
    public void setStatisticsLevel(Statistics level) {
        set(level);
    }

    /**
     * @return true if the configuration option is an array.
     */
    public boolean isArray() {
        return this.isType(ARRAY);
    }

    /**
     * @return true if the configuration option is a value.
     */
    public boolean isValue() {
        return this.isType(VALUE);
    }

    /**
     * @return true if the configuration option is a map.
     */
    public boolean isMap() {
        return this.isType(MAP);
    }

    public MemorySegment getPointer() {
        return configuration;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (isMap()) {
            int size = mapSize();
            List<String> keys = getKeys();
            builder.append("{");
            for (int i = 0; i < size; i++) {
                String name = keys.get(i);
                builder.append('"').append(name).append('"');
                builder.append(": ");
                builder.append(get(name));
                if (i < size - 1)
                    builder.append(", ");
            }
            builder.append("}");
        }

        if (isArray()) {
            int size = arraySize();
            builder.append("[");
            for (int i = 0; i < size; i++) {
                builder.append(get(i).toString());
                if (i < size - 1)
                    builder.append(", ");
            }
            builder.append("]");
        }

        if (isValue()) {
            builder.append('"').append(get()).append('"');
            return builder.toString();
        }

        return builder.toString();
    }
}
