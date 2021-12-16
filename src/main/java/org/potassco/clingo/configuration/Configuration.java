package org.potassco.clingo.configuration;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.control.Control;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.configuration.args.Option;
import org.potassco.clingo.internal.NativeSizeByReference;
import org.potassco.clingo.statistics.Statistics;

import java.util.ArrayList;
import java.util.List;

import static org.potassco.clingo.configuration.ConfigurationType.Type.*;

/**
 * Allows for changing the configuration of the underlying solver.
 * <p>
 * Options are organized hierarchically. To change and inspect an option use:
 * <code>
 * config.group.subgroup.option = "value"
 * value = config.group.subgroup.option
 * </code>
 * There are also arrays of option groups that can be accessed using integer
 * indices:
 * <p>
 * config.group.subgroup[0].option = "value1"
 * config.group.subgroup[1].option = "value2"
 * <p>
 * To list the subgroups of an option group, use the `Configuration.keys`
 * member. Array option groups, like solver, can be iterated. Furthermore,
 * there are meta options having key `configuration`. Assigning a meta option
 * sets a number of related options.  To get further information about an
 * option or option group, use `Configuration.description`.
 * <p>
 * The value of an option is always a string and any value assigned to an
 * option is automatically converted into a string.
 */
public class Configuration {

    private final Pointer configuration;
    private final int key;
    private ConfigurationType typeBitset;

    public Configuration(Control control) {
        PointerByReference configurationRef = new PointerByReference();
        Clingo.check(Clingo.INSTANCE.clingo_control_configuration(control.getPointer(), configurationRef));
        this.configuration = configurationRef.getValue();

        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_root(configuration, intByReference));
        this.key = intByReference.getValue();
    }

    public Configuration(Pointer configuration, int key) {
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
            IntByReference intByReference = new IntByReference();
            Clingo.check(Clingo.INSTANCE.clingo_configuration_type(configuration, key, intByReference));
            typeBitset = new ConfigurationType(intByReference.getValue());
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
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_array_size(configuration, key, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
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
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_map_size(configuration, key, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
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

        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_map_has_subkey(configuration, key, name, byteByReference));
        if (byteByReference.getValue() == 0)
            throw new IllegalStateException("configuration entry does not have option '" + name + "'");

        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_map_at(configuration, key, name, intByReference));
        int key = intByReference.getValue();

        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_configuration_description(configuration, key, stringByReference));
        return stringByReference[0];
    }

    private int getSubkey(String name) {
        if (!isMap())
            throw new IllegalStateException("configuration entry is not a map");
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_map_has_subkey(configuration, key, name, byteByReference));
        if (byteByReference.getValue() == 0)
            throw new IllegalStateException("configuration entry does not have option '" + name + "'");
        IntByReference intByReference = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_map_at(configuration, key, name, intByReference));
        return intByReference.getValue();
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
        String[] stringByReference = new String[1];
        for (int i = 0; i < mapSize; i++) {
            Clingo.check(Clingo.INSTANCE.clingo_configuration_map_subkey_name(configuration, key, new NativeSize(i), stringByReference));
            keys.add(stringByReference[0]);
        }

        return keys;
    }

    /**
     * Check whether an entry has a value.
     * <p>
     * The {@link ConfigurationType type} of the entry must be {@link ConfigurationType.Type#VALUE}.
     *
     * @return whether the entry has a value
     */
    public boolean isAssigned() {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_value_is_assigned(configuration, key, byteByReference));
        return byteByReference.getValue() > 0;
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
        IntByReference keyIdentifier = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_map_at(configuration, key, name, keyIdentifier));
        return new Configuration(configuration, keyIdentifier.getValue());
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
        if (!isType(MAP))
            throw new IllegalStateException("Configuration entry is not an array");
        IntByReference keyIdentifier = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_array_at(configuration, key, new NativeSize(index), keyIdentifier));
        return new Configuration(configuration, keyIdentifier.getValue());
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
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_value_get_size(configuration, key, nativeSizeByReference));
        int valueSize = (int) nativeSizeByReference.getValue();
        byte[] valueBytes = new byte[valueSize];
        Clingo.check(Clingo.INSTANCE.clingo_configuration_value_get(configuration, key, valueBytes, new NativeSize(valueSize)));
        return Native.toString(valueBytes);
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
        IntByReference keyIdentifier = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_map_at(configuration, key, name, keyIdentifier));
        Clingo.check(Clingo.INSTANCE.clingo_configuration_value_set(configuration, keyIdentifier.getValue(), value));
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
        IntByReference keyIdentifier = new IntByReference();
        Clingo.check(Clingo.INSTANCE.clingo_configuration_array_at(configuration, key, new NativeSize(index), keyIdentifier));
        Clingo.check(Clingo.INSTANCE.clingo_configuration_value_set(configuration, keyIdentifier.getValue(), value));
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
        set("stats", level.toString());
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

    public Pointer getPointer() {
        return configuration;
    }
}
