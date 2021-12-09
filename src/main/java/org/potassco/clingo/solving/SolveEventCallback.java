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
        if (type == SolveEventType.MODEL) {
            onModel(new Model(event));
        } else if (type == SolveEventType.UNSAT) {
            Pointer symbolsPointer = event.getPointer(0);
            int symbolsSize = getUnsatAmountSymbols(event);
            // TODO: solver_literals are int32, why return value of int64?!
//            List<Symbol> unsatSymbols = Arrays.stream(symbolsPointer.getLongArray(0, symbolsSize))
//                    .mapToObj(Symbol::fromLong)
//                    .collect(Collectors.toList());
//            Control control = new Control(data);
//            for (SymbolicAtom symbolicAtom : control.getSymbolicAtoms()) {
//                System.out.println(symbolicAtom.getLiteral());
//            }
            List<Integer> unsatSymbols = new ArrayList<>();
            long[] longs = symbolsSize == 0 ? new long[0] : symbolsPointer.getLongArray(0, symbolsSize);
            for (int i = 0; i < symbolsSize; i++) {
                unsatSymbols.add((int) longs[i]);
            }
            onUnsat(unsatSymbols);
        } else if (type == SolveEventType.STATISTICS) {
            Statistics perStep = new Statistics(event.getPointer(0));
            Statistics accumulated = new Statistics(event.getPointer(Native.POINTER_SIZE));
            onStatistics(perStep, accumulated);
        } else if (type == SolveEventType.FINISH) {
            onResult(new SolveResult(event.getInt(0)));
        } else {
            throw new IllegalStateException("unknown solve event type " + type);
        }
        return true;
    }

    public void onModel(Model model) {

    }

    public void onUnsat(List<Integer> literals) {

    }

    public void onStatistics(Statistics perStep, Statistics accumulated) {

    }

    public void onResult(SolveResult solveResult) {

    }

    // TODO: check if this is implemented correctly
    private int getUnsatAmountSymbols(Pointer event) {
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
