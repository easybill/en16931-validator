package io.github.easybill;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "EN16931 Validator API",
        version = "0.1.0",
        contact = @Contact(
            name = "easybill GmbH",
            url = "https://github.com/easybill",
            email = "dev@easybill.de"
        ),
        license = @License(name = "MIT", url = "https://mit-license.org")
    )
)
public final class EN16931ValidatorApplication extends Application {}
