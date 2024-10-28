package io.github.easybill.Exceptions;

import org.checkerframework.checker.nullness.qual.NonNull;

abstract class ValidatorException extends RuntimeException {

    public ValidatorException() {
        super();
    }

    public ValidatorException(Throwable cause) {
        super(cause);
    }

    public ValidatorException(@NonNull String message, Throwable cause) {
        super(message, cause);
    }
}
