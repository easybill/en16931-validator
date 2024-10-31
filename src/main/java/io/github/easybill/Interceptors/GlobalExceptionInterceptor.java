package io.github.easybill.Interceptors;

import io.github.easybill.Contracts.IExceptionNotifier;
import io.github.easybill.Exceptions.InvalidXmlException;
import io.github.easybill.Exceptions.ParsingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public final class GlobalExceptionInterceptor
    implements ExceptionMapper<Throwable> {

    private final IExceptionNotifier exceptionNotifier;

    private static final Logger logger = Logger.getLogger(
        GlobalExceptionInterceptor.class
    );

    public GlobalExceptionInterceptor(IExceptionNotifier exceptionNotifier) {
        this.exceptionNotifier = exceptionNotifier;
    }

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            return ((WebApplicationException) exception).getResponse();
        }

        if (exception instanceof InvalidXmlException) {
            return Response.status(422, "Unprocessable Content").build();
        }

        if (exception instanceof ParsingException) {
            return Response
                .status(422, "Unprocessable Content - Parsing Error")
                .build();
        }

        exceptionNotifier.notify(exception);

        logger.error("Encountered an exception:", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
