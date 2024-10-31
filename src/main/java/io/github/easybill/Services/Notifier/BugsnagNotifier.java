package io.github.easybill.Services.Notifier;

import com.bugsnag.Bugsnag;
import io.github.easybill.Contracts.IApplicationConfig;
import io.github.easybill.Contracts.IExceptionNotifier;
import io.github.easybill.Contracts.IStageService;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.jboss.logging.Logger;

@ApplicationScoped
public final class BugsnagNotifier implements IExceptionNotifier {

    private final IStageService stageService;
    private final Optional<String> bugsnagApiKey;

    private static final Logger logger = Logger.getLogger(
        BugsnagNotifier.class
    );

    public BugsnagNotifier(
        IApplicationConfig config,
        IStageService stageService
    ) {
        this.bugsnagApiKey = config.exceptions().bugsnagApiKey();
        this.stageService = stageService;

        if (this.bugsnagApiKey.isEmpty()) {
            logger.info("Bugsnag API key is not set. Won't notify exceptions");
        }

        if (!this.stageService.isProd()) {
            logger.info(
                "Bugsnag won't notify exceptions as the application is not in the prod profile."
            );
        }
    }

    public void notify(Throwable throwable) {
        if (this.bugsnagApiKey.isEmpty()) {
            return;
        }

        if (!stageService.isProd()) {
            return;
        }

        try (
            Bugsnag bugsnag = new Bugsnag(
                this.bugsnagApiKey.orElseThrow(RuntimeException::new)
            )
        ) {
            bugsnag.notify(throwable);
        }
    }
}
