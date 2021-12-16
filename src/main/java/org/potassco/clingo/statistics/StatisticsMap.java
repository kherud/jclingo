package org.potassco.clingo.statistics;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.internal.Clingo;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

public class StatisticsMap extends Statistics {

    public StatisticsMap(Pointer statistics, long key) {
        super(statistics, key);
    }

    /**
     * Get the number of subkeys of a map entry.
     *
     * @return the resulting number
     */
    public int size() {
        NativeSizeByReference nativeSizeByReference = new NativeSizeByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_size(statistics, key, nativeSizeByReference));
        return (int) nativeSizeByReference.getValue();
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
        String[] stringByReference = new String[1];
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_subkey_name(statistics, key, new NativeSize(index), stringByReference));
        return stringByReference[0];
    }

    /**
     * Test if the given map contains a specific subkey.
     *
     * @param name the name of the key
     * @return true if the map has a subkey with the given name
     */
    public boolean hasKey(String name) {
        ByteByReference byteByReference = new ByteByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_has_subkey(statistics, key, name, byteByReference));
        return byteByReference.getValue() > 0;
    }

    /**
     * Add a subkey with the given name.
     *
     * @param name       the name of the new subkey
     * @param type       the type of the new subkey
     * @return the resulting entry
     */
    public Statistics addKey(String name, StatisticsType type) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_add_subkey(statistics, key, name, type.getValue(), longByReference));
        long subKey = longByReference.getValue();
        return fromKey(statistics, subKey, type);
    }

    /**
     * Set a value of the map for the specified key
     *
     * @param name the key name
     * @param value the raw value
     * @return the newly created entry
     */
    public StatisticsValue set(String name, double value) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_add_subkey(statistics, key, name, StatisticsType.VALUE.getValue(), longByReference));
        long subKey = longByReference.getValue();
        return new StatisticsValue(statistics, subKey, value);
    }

    /**
     * Set an array of values for the specified map key
     *
     * @param name the key name
     * @param values the raw values
     * @return the newly created entry
     */
    public StatisticsArray set(String name, double[] values) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_add_subkey(statistics, key, name, StatisticsType.ARRAY.getValue(), longByReference));
        long subKey = longByReference.getValue();
        return new StatisticsArray(statistics, subKey, values);
    }

    /**
     * Set a statistics object for the specified map key
     *
     * @param name the key name
     * @param entry the statistics object
     * @return the newly created entry
     */
    public Statistics set(String name, Statistics entry) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_add_subkey(statistics, key, name, entry.getType().getValue(), longByReference));
        return Statistics.fromKey(statistics, longByReference.getValue());
    }

    /**
     * Lookup a subkey under the given name.
     * <p>
     * Multiple levels can be looked up by concatenating keys with a period.
     *
     * @param name       the name to lookup the subkey
     * @return     the resulting subkey
     */
    private long getSubKey(String name) {
        LongByReference longByReference = new LongByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_map_at(statistics, key, name, longByReference));
        return longByReference.getValue();
    }

    /**
     * @return the type of this statistics object
     */
    @Override
    public StatisticsType getType() {
        return StatisticsType.MAP;
    }
}
