package io.github.easybill.Contracts;

import static io.smallrye.config.ConfigMapping.NamingStrategy.VERBATIM;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;
import java.util.Optional;

@StaticInitSafe
@ConfigMapping(prefix = "app", namingStrategy = VERBATIM)
public interface IApplicationConfig {
    String version();

    Artefacts artefacts();

    interface Artefacts {
        String version();
    }

    Exceptions exceptions();

    interface Exceptions {
        @WithName("bugsnag-api-key")
        Optional<String> bugsnagApiKey();
    }
}
