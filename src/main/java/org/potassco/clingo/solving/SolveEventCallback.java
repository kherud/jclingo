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

package org.potassco.clingo.solving;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import org.potassco.clingo.statistics.Statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Callback function called during search to notify when the search is finished or a model is ready.
 * <p>
 * If a (non-recoverable) clingo API function fails in this callback, it must return false.
 * In case of errors not related to clingo, set error code ::clingo_error_unknown and return false to stop solving with an error.
 * <p>
 * The event is either a pointer to a model, a pointer to an int64_t* and a size_t, a pointer to two statistics objects (per step and accumulated statistics), or a solve result.
 * If the search is finished, the model is NULL.
 * <p>
 * You have to inherit from this class to implement your own callback.
 */
public abstract class SolveEventCallback implements Callback {
    /**
     * @param event the current event.
     * @param data  user data of the callback
     * @param goon  can be set to false to stop solving
     * @return whether the call was successful
     */
    public boolean callback(int code, Pointer event, Pointer data, ByteByReference goon) {
        SolveEventType type = SolveEventType.fromValue(code);
        switch (type) {
            case MODEL:
                onModel(new Model(event));
                break;
            case UNSAT:
                Pointer costPointer = event.getPointer(0);
                int costSize = getUnsatCostSize(event);
                long[] unsatCost = costSize == 0 ? new long[0] : costPointer.getLongArray(0, costSize);
                onUnsat(unsatCost);
                break;
            case STATISTICS:
                Statistics perStep = Statistics.fromPointer(event.getPointer(0));
                Statistics accumulated = Statistics.fromPointer(event.getPointer(Native.POINTER_SIZE));
                onStatistics(perStep, accumulated);
                break;
            case FINISH:
                onResult(new SolveResult(event.getInt(0)));
                break;
            default:
                throw new IllegalStateException("unknown solve event type " + type);
        }
        return true;
    }

    public void onModel(Model model) {

    }

    public void onUnsat(long[] cost) {

    }

    public void onStatistics(Statistics perStep, Statistics accumulated) {

    }

    public void onResult(SolveResult solveResult) {

    }

    // TODO: check if this is implemented correctly
    private int getUnsatCostSize(Pointer event) {
        switch (Native.SIZE_T_SIZE) {
            case 2:
                return event.getShort(Native.POINTER_SIZE);
            case 4:
                return event.getInt(Native.POINTER_SIZE);
            case 8:
                return (int) event.getLong(Native.POINTER_SIZE);
            default:
                throw new IllegalStateException("unknown native size: " + Native.SIZE_T_SIZE);
        }
    }
}
