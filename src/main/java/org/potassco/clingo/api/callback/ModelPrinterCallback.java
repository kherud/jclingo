package org.potassco.clingo.api.callback;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback to customize model printing.
 */
public abstract class ModelPrinterCallback implements Callback {
    /**
     * Callback to print a model in default format.
     */
    public interface DefaultModelPrinter extends Callback {
        /**
         * @param data user data for the callback
         * @return whether the call was successful
         */
        boolean callback(Pointer data);
    }

    /**
     * @param model the model
     * @param printer the default model printer
     * @param printerData user data for the printer
     * @param data user data for the callback
     * @return whether the call was successful
     */
    public boolean callback(Pointer model, DefaultModelPrinter printer, Pointer printerData, Pointer data) {
        return call(model, printer, printerData, data);
    }

    public abstract boolean call(Pointer model, DefaultModelPrinter printer, Pointer printerData, Pointer data);
}
