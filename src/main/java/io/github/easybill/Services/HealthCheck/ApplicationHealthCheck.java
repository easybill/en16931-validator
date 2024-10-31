package io.github.easybill.Services.HealthCheck;

import io.github.easybill.Contracts.IApplicationConfig;
import jakarta.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public final class ApplicationHealthCheck implements HealthCheck {

    final IApplicationConfig config;

    public ApplicationHealthCheck(IApplicationConfig config) {
        this.config = config;
    }

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named(
            "Application"
        );

        var osBean = ManagementFactory.getOperatingSystemMXBean();
        var memBean = ManagementFactory.getMemoryMXBean();

        return response
            .up()
            .withData("version", Objects.requireNonNull(config.version()))
            .withData("osName", osBean.getName())
            .withData("osArch", osBean.getArch())
            .withData(
                "heapMemoryUsageMax",
                memBean.getHeapMemoryUsage().getMax()
            )
            .withData(
                "heapMemoryUsageCommitted",
                memBean.getHeapMemoryUsage().getCommitted()
            )
            .withData(
                "heapMemoryUsageUsed",
                memBean.getHeapMemoryUsage().getUsed()
            )
            .build();
    }
}
