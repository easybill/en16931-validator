package io.github.easybill.Exceptions;

public class ParsingException extends ValidatorException {

    public ParsingException(Throwable cause) {
        super("could not parse exception", cause);
    }
}
