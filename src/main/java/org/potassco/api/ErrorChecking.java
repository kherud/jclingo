/*
 * Copyright (C) 2021 denkbares GmbH. All rights reserved.
 */

package org.potassco.api;

import org.potassco.api.enums.ErrorCode;

/**
 * Each method of the clingo API returns a boolean, if the call was successful.
 * If it is false, we have to check the error message and code.
 * This is nasty, is there are better way?!
 */
public interface ErrorChecking {
    default void checkError(boolean callSuccess) {
        if (!callSuccess) {
            String errorMessage = Clingo.INSTANCE.clingo_error_message();
            ErrorCode errorCode = Clingo.INSTANCE.clingo_error_code();
            throw new RuntimeException(String.format("[%s] %s", errorCode.name(), errorMessage));
        }
    }
}
