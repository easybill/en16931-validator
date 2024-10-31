package io.github.easybill.Services;

import io.github.easybill.Contracts.IStageService;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.eclipse.microprofile.config.Config;

@ApplicationScoped
public final class StageService implements IStageService {

    private final List<String> profiles;

    public StageService(Config config) {
        this.profiles = ((SmallRyeConfig) config).getProfiles();
    }

    @Override
    public boolean isProd() {
        return profiles.contains("prod");
    }

    @Override
    public boolean isDev() {
        return profiles.contains("dev");
    }

    @Override
    public boolean isTest() {
        return profiles.contains("test");
    }
}
