package io.github.easybill.Interceptors;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import org.jboss.logging.Logger;

@Provider
public final class RequestResponseLoggingInterceptor
    implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = Logger.getLogger(
        RequestResponseLoggingInterceptor.class
    );

    @Override
    public void filter(ContainerRequestContext containerRequestContext)
        throws IOException {
        String method = containerRequestContext.getMethod();
        String uri = containerRequestContext
            .getUriInfo()
            .getRequestUri()
            .toString();
        String queryParams = containerRequestContext
            .getUriInfo()
            .getQueryParameters()
            .toString();

        logger.infof("Request received: %s %s, %s", method, uri, queryParams);
    }

    @Override
    public void filter(
        ContainerRequestContext containerRequestContext,
        ContainerResponseContext containerResponseContext
    ) throws IOException {
        int statusCode = containerResponseContext.getStatus();
        String status = containerResponseContext
            .getStatusInfo()
            .getReasonPhrase();

        logger.infof("Response sent: %s - %s", statusCode, status);
    }
}
