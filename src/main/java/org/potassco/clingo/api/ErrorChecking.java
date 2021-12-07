/*
 * Copyright (C) 2021 denkbares GmbH. All rights reserved.
 */

package org.potassco.clingo.api;

import org.potassco.clingo.api.types.ErrorCode;

/**
 * Each method of the clingo API returns a boolean, if the call was successful.
 * If it is false, we have to check the error message and code.
 * This is nasty, is there are better way?!
 */
public interface ErrorChecking {
    default void checkError(boolean callSuccess) {
        if (!callSuccess) {
            String errorMessage = Clingo.INSTANCE.clingo_error_message();
            int errorId = Clingo.INSTANCE.clingo_error_code();
            ErrorCode errorCode = ErrorCode.fromValue(errorId);
            throw new RuntimeException(String.format("[%s] %s", errorCode.name(), errorMessage));
        }
    }

    static void staticCheckError(boolean callSuccess) {
        if (!callSuccess) {
            String errorMessage = Clingo.INSTANCE.clingo_error_message();
            int errorId = Clingo.INSTANCE.clingo_error_code();
            ErrorCode errorCode = ErrorCode.fromValue(errorId);
            throw new RuntimeException(String.format("[%s] %s", errorCode.name(), errorMessage));
        }
    }
}
