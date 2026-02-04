package com.terransky.p3timerjda.utilities.exceptions;

@SuppressWarnings("unused")
public class FailedInteractionException extends RuntimeException {

    public FailedInteractionException() {
    }

    public FailedInteractionException(String message) {
        super(message);
    }

    public FailedInteractionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedInteractionException(Throwable cause) {
        super(cause);
    }

    protected FailedInteractionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
