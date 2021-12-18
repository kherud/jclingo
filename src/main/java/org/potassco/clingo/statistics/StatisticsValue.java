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
