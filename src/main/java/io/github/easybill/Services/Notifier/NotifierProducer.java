package io.github.easybill.Services.Notifier;

import io.github.easybill.Contracts.IApplicationConfig;
import io.github.easybill.Contracts.IExceptionNotifier;
import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.Produces;

@Dependent
public final class NotifierProducer {

    @Produces
    @ApplicationScoped
    @IfBuildProfile("prod")
    public IExceptionNotifier realNotifier(IApplicationConfig config) {
        var apiKey = config.exceptions().bugsnagApiKey();

        if (apiKey.isEmpty()) {
            return new NoopNotifier();
        }

        return new BugsnagNotifier(apiKey.get());
    }

    @Produces
    @DefaultBean
    @ApplicationScoped
    public IExceptionNotifier noopNotifier() {
        return new NoopNotifier();
    }
}
