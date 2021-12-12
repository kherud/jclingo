package org.potassco.clingo.solving;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * Callback to customize model printing.
 */
public interface ModelPrinterCallback extends Callback {
    /**
     * Callback to print a model in default format.
     */
    interface DefaultModelPrinter extends Callback {
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
    default boolean callback(Pointer model, DefaultModelPrinter printer, Pointer printerData, Pointer data) {
        call(new Model(model));
        return true;
    }

    void call(Model model);
}
