package io.github.easybill.Controllers;

import io.github.easybill.Contracts.IValidationService;
import io.github.easybill.Dtos.ValidationResult;
import io.github.easybill.Exceptions.InvalidXmlException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/")
public final class ValidationController {

    private final IValidationService validationService;

    public ValidationController(IValidationService validationService) {
        this.validationService = validationService;
    }

    @POST
    @Path("/validation")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponses(
        {
            @APIResponse(
                responseCode = "200",
                description = "The submitted XML is valid "
            ),
            @APIResponse(
                responseCode = "400",
                description = "Schematron validation for the submitted XML failed. Response will contain the failed assertions"
            ),
        }
    )
    public RestResponse<@NonNull ValidationResult> validation(
        InputStream xmlInputStream
    ) throws Exception {
        try {
            ValidationResult result = validationService.validateXml(
                xmlInputStream
            );

            if (result.isValid()) {
                return RestResponse.ResponseBuilder
                    .ok(result, MediaType.APPLICATION_JSON)
                    .build();
            }

            return RestResponse.ResponseBuilder
                .create(RestResponse.Status.BAD_REQUEST, result)
                .type(MediaType.APPLICATION_JSON)
                .build();
        } catch (InvalidXmlException exception) {
            return RestResponse.status(RestResponse.Status.BAD_REQUEST);
        }
    }
}
