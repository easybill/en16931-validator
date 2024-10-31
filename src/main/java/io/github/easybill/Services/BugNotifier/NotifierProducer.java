package io.github.easybill.Services.BugNotifier;

import io.github.easybill.Contracts.IApplicationConfig;
import io.github.easybill.Contracts.IExceptionNotifier;
import io.github.easybill.Interceptors.GlobalExceptionInterceptor;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.Produces;
import org.jboss.logging.Logger;

@Dependent
public final class NotifierProducer {

    private static final Logger logger = Logger.getLogger(
        GlobalExceptionInterceptor.class
    );

    @Produces
    @ApplicationScoped
    @IfBuildProfile("prod")
    public IExceptionNotifier realNotifier(IApplicationConfig config) {
        var bugsnagApiKey = config.exceptions().bugsnagApiKey();

        if (bugsnagApiKey.isEmpty()) {
            logger.info(
                "Notifier: NOOP notifier loaded as no API-Key was provided"
            );

            return new NoopNotifier();
        }

        logger.info("Notifier: BUGSNAG notifier loaded");

        return new BugsnagNotifier(bugsnagApiKey.get());
    }

    @Produces
    @DefaultBean
    @ApplicationScoped
    public IExceptionNotifier noopNotifier() {
        logger.info(
            "Notifier: NOOP notifier loaded as the build profile does not permit loading a real notifier"
        );

        return new NoopNotifier();
    }
}
