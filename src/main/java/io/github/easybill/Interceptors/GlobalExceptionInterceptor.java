package io.github.easybill.Interceptors;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionInterceptor implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(
        GlobalExceptionInterceptor.class
    );

    @Override
    public Response toResponse(Throwable exception) {
        LOGGER.error("Encountered an exception:", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}
