package io.github.easybill.Services.HealthCheck;

import jakarta.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public final class ApplicationHealthCheck implements HealthCheck {

    final Config config;

    public ApplicationHealthCheck(Config config) {
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
            .withData(
                "version",
                Objects.requireNonNull(
                    config.getConfigValue("application.version").getValue()
                )
            )
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
