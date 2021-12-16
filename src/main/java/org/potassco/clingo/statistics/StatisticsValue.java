package org.potassco.clingo.statistics;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.DoubleByReference;
import org.potassco.clingo.internal.Clingo;

public class StatisticsValue extends Statistics {

    public StatisticsValue(Pointer statistics, long key) {
        super(statistics, key);
    }

    public StatisticsValue(Pointer statistics, long key, double value) {
        super(statistics, key);
        set(value);
    }

    /**
     * Get the value of the given entry.
     *
     * @return the value of the entry
     */
    @Override
    public double get() {
        DoubleByReference doubleByReference = new DoubleByReference();
        Clingo.check(Clingo.INSTANCE.clingo_statistics_value_get(statistics, key, doubleByReference));
        return doubleByReference.getValue();
    }

    /**
     * Set the value of the given entry.
     *
     * @param value the new value
     */
    public void set(double value) {
        Clingo.check(Clingo.INSTANCE.clingo_statistics_value_set(statistics, key, value));
    }

    /**
     * @return the type of this statistics object
     */
    @Override
    public StatisticsType getType() {
        return StatisticsType.VALUE;
    }
}
