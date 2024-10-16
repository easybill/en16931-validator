package io.github.easybill.Services.HealthCheck;

import io.github.easybill.Contracts.IValidationService;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Objects;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public final class ValidatorHealthCheck implements HealthCheck {

    final Config config;
    final IValidationService validationService;

    public ValidatorHealthCheck(
        IValidationService validationService,
        Config config
    ) {
        this.config = config;
        this.validationService = validationService;
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named(
            "Validator"
        );

        response.withData(
            "artefactsVersion",
            Objects.requireNonNull(
                config.getConfigValue("en16931.artefacts.version").getValue()
            )
        );

        if (this.validationService.isLoadedSchematronValid()) {
            return response.up().build();
        }

        return response.down().build();
    }
}
