package io.github.easybill.Services.HealthCheck;

import jakarta.enterprise.context.ApplicationScoped;
import java.lang.management.ManagementFactory;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@ApplicationScoped
public final class StatsHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder response = HealthCheckResponse.named(
            "stats"
        );

        var osBean = ManagementFactory.getOperatingSystemMXBean();
        var memBean = ManagementFactory.getMemoryMXBean();

        return response
            .up()
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
