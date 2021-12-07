package org.potassco.clingo.control;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.potassco.clingo.api.Clingo;
import org.potassco.clingo.api.ErrorChecking;
import org.potassco.clingo.api.types.ConfigurationType;
import org.potassco.clingo.api.types.NativeSize;
import org.potassco.clingo.args.Option;

import java.nio.charset.StandardCharsets;

/**
 * This class maps clingo's native configuration structure, which is used to configure the grounder / solver
 */
public class Configuration extends JSONLike implements ErrorChecking {

    private final Pointer configuration;
    private final int rootKey;

    private final ByteByReference byteByRef = new ByteByReference();

    public Configuration(Control controlObject) {
        // obtain pointer to the configuration object
        PointerByReference configurationRef = new PointerByReference();

        checkError(Clingo.INSTANCE.clingo_control_configuration(controlObject.getPointer(), configurationRef));
        this.configuration = configurationRef.getValue();

        // get root of the configuration tree map
        checkError(Clingo.INSTANCE.clingo_configuration_root(configuration, this.intByRef));
        this.rootKey = this.intByRef.getValue();
    }

    /**
     * Sets clingo's native configuration to a list of options {@link Option}
     * @param options an array of options
     */
    public void setOptions(Option... options) {
        for (Option option : options) {
            setOption(option);
        }
    }

    /**
     * Resets a list of options to clingo's defaults
     * @param options an array of options
     */
    public void resetOptions(Option... options) {
        for (Option option : options) {
            setOption(option.getDefault());
        }
    }

    /**
     * Resets an option to clingo's defaults
     * @param option the option
     */
    public void resetOption(Option option) {
        setOption(option.getDefault());
    }

    /**
     * Returns a value from clingo's native configuration for a key specified by {@link Option}
     * @param option option object with the key
     * @return the corresponding string value
     */
    public String getValue(Option option) {
        return getValue(option.getNativeKey());
    }

    /**
     * Sets clingo's native configuration to a value, with a key specified by {@link Option}
     * @param option option object with the key and value
     */
    public void setOption(Option option) {
        String key = option.getNativeKey();
        String value = option.getValue();
        setValue(key, value);
    }

    /**
     * Returns a value from clingo's native configuration for a key string
     * @param key the string key
     * @return the corresponding string value
     */
    public String getValue(String key) {
        IntByReference keyIdentifier = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_configuration_map_at(configuration, rootKey, key, keyIdentifier));
        return (String) getValueByKey(keyIdentifier.getValue());
    }

    /**
     * Sets clingo's native configuration to a key/value string
     * @param key the string key
     */
    public void setValue(String key, String value) {
        IntByReference keyIdentifier = new IntByReference();
        checkError(Clingo.INSTANCE.clingo_configuration_map_at(configuration, rootKey, key, keyIdentifier));
        checkError(Clingo.INSTANCE.clingo_configuration_value_set(configuration, keyIdentifier.getValue(), value));
    }

    /**
     * Sets the level of clingo's statistics output to a value specified by {@link Statistics}
     * @param level the level of output
     */
    public void setStatisticsLevel(Statistics level) {
        setValue("stats", level.toString());
    }

    public Pointer getPointer() {
        return configuration;
    }

    @Override
    protected long getRootKey() {
        return rootKey;
    }

    @Override
    protected int getKeyType(long key) {
        checkError(Clingo.INSTANCE.clingo_configuration_type(configuration, (int) key, intByRef));
        return intByRef.getValue();
    }

    @Override
    protected boolean isMap(int type) {
        return (ConfigurationType.isType(type, ConfigurationType.Type.MAP));
    }

    @Override
    protected boolean isArray(int type) {
        return (ConfigurationType.isType(type, ConfigurationType.Type.ARRAY));
    }

    @Override
    protected boolean isValue(int type) {
        return (ConfigurationType.isType(type, ConfigurationType.Type.VALUE));
    }

    @Override
    protected long getMapSize(long key) {
        checkError(Clingo.INSTANCE.clingo_configuration_map_size(configuration, (int) key, nativeSizeByRef));
        return nativeSizeByRef.getValue();
    }

    @Override
    protected long getArraySize(long key) {
        checkError(Clingo.INSTANCE.clingo_configuration_array_size(configuration, (int) key, nativeSizeByRef));
        return nativeSizeByRef.getValue();
    }

    @Override
    protected String getNameAtMapIndex(long key, int index) {
        checkError(Clingo.INSTANCE.clingo_configuration_map_subkey_name(configuration, (int) key, new NativeSize(index), stringByRef));
        return stringByRef[0];
    }

    @Override
    protected long getIdAtArrayIndex(long key, int index) {
        checkError(Clingo.INSTANCE.clingo_configuration_array_at(configuration, (int) key, new NativeSize(index), intByRef));
        return intByRef.getValue();
    }

    @Override
    protected long getIdOfKeyName(long key, String name) {
        checkError(Clingo.INSTANCE.clingo_configuration_map_at(configuration, (int) key, name, intByRef));
        return intByRef.getValue();
    }

    @Override
    protected boolean checkValueAssigned(long key) {
        checkError(Clingo.INSTANCE.clingo_configuration_value_is_assigned(configuration, (int) key, byteByRef));
        return byteByRef.getValue() == 1;
    }

    @Override
    protected Object getValueByKey(long key) {
        checkError(Clingo.INSTANCE.clingo_configuration_value_get_size(configuration, (int) key, nativeSizeByRef));
        byte[] valueBytes = new byte[(int) nativeSizeByRef.getValue()];
        checkError(Clingo.INSTANCE.clingo_configuration_value_get(configuration, (int) key, valueBytes, new NativeSize(nativeSizeByRef.getValue())));
        String valueString = new String(valueBytes, StandardCharsets.UTF_8);
        // remove zero terminator char
        return valueString.substring(0, valueBytes.length - 1);
    }
}
