package io.github.easybill.Exceptions;

public class InvalidXmlException extends ValidatorException {

    public InvalidXmlException() {
        super();
    }

    public InvalidXmlException(Throwable cause) {
        super("the xml is invalid", cause);
    }
}
