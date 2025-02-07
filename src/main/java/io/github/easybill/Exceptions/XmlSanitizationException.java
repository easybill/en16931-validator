package io.github.easybill.Exceptions;

public class XmlSanitizationException extends ValidatorException {

    public XmlSanitizationException(Throwable cause) {
        super("could not sanitize the xml accordingly", cause);
    }
}
