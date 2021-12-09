package org.potassco.clingo.statistics;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.potassco.clingo.Clingo;
import org.potassco.clingo.ErrorChecking;
import org.potassco.clingo.internal.NativeSize;
import org.potassco.clingo.internal.NativeSizeByReference;

/**
 * This class maps clingo's native statistics structure
 */
public class Statistics implements ErrorChecking {

    private final Pointer statistics;
    private final long rootKey;

    private final NativeSizeByReference nativeSizeByRef = new NativeSizeByReference();
    private final DoubleByReference doubleByRef = new DoubleByReference();
    private final String[] stringByRef = new String[1];
    private final LongByReference longByRef = new LongByReference();
    private final IntByReference intByRef = new IntByReference();

    public Statistics(Pointer statistics) {
        this.statistics = statistics;

        // get root of the configuration tree map
        checkError(Clingo.INSTANCE.clingo_statistics_root(statistics, this.longByRef));
        this.rootKey = this.longByRef.getValue();
    }

    private long getRootKey() {
        return rootKey;
    }

    private StatisticsType getKeyType(long key) {
        checkError(Clingo.INSTANCE.clingo_statistics_type(statistics, key, intByRef));
        return StatisticsType.fromValue(intByRef.getValue());
    }

    private boolean isMap(StatisticsType type) {
        return type == StatisticsType.MAP;
    }

    private boolean isArray(StatisticsType type) {
        return type == StatisticsType.ARRAY;
    }

    private boolean isValue(StatisticsType type) {
        return type == StatisticsType.VALUE;
    }

    private long getMapSize(long key) {
        checkError(Clingo.INSTANCE.clingo_statistics_map_size(statistics, key, nativeSizeByRef));
        return nativeSizeByRef.getValue();
    }

    private long getArraySize(long key) {
        checkError(Clingo.INSTANCE.clingo_statistics_array_size(statistics, key, nativeSizeByRef));
        return nativeSizeByRef.getValue();
    }

    private String getNameAtMapIndex(long key, int index) {
        checkError(Clingo.INSTANCE.clingo_statistics_map_subkey_name(statistics, key, new NativeSize(index), stringByRef));
        return stringByRef[0];
    }

    private long getIdAtArrayIndex(long key, int index) {
        checkError(Clingo.INSTANCE.clingo_statistics_array_at(statistics, key, new NativeSize(index), longByRef));
        return longByRef.getValue();
    }

    private long getIdOfKeyName(long key, String name) {
        checkError(Clingo.INSTANCE.clingo_statistics_map_at(statistics, key, name, longByRef));
        return longByRef.getValue();
    }

    private boolean checkValueAssigned(long key) {
        return true;
    }

    private Object getValueByKey(long key) {
        checkError(Clingo.INSTANCE.clingo_statistics_value_get(statistics, key, doubleByRef));
        return doubleByRef.getValue();
    }
}
