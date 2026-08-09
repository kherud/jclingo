package org.potassco.clingo.internal;

import org.potassco.clingo.control.ErrorCode;

/**
 * Thrown when a clingo API call reports failure.
 */
public class ClingoRuntimeException extends RuntimeException {

    private final ErrorCode errorCode;

    public ClingoRuntimeException(ErrorCode errorCode, String message) {
        super(String.format("[%s] %s", errorCode.name(), message));
        this.errorCode = errorCode;
    }

    public ClingoRuntimeException(ErrorCode errorCode, String message, Throwable cause) {
        super(String.format("[%s] %s", errorCode.name(), message), cause);
        this.errorCode = errorCode;
    }

    /**
     * @return the code clingo reported, {@link ErrorCode#UNKNOWN} if it reported one this binding does not know
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
