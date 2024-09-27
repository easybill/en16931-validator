package io.github.easybill.Services.HealthCheck;

import io.github.easybill.Contracts.IValidationService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public final class ValidatorHealthCheck implements HealthCheck {

    final IValidationService validationService;

    public ValidatorHealthCheck(IValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named(
            "schematron ready and ruleset is valid"
        );

        if (this.validationService.isLoadedSchematronValid()) {
            return response.up().build();
        }

        return response.down().build();
    }
}
