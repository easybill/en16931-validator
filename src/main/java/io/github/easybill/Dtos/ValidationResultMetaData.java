package io.github.easybill.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.easybill.Enums.XMLSyntaxType;
import org.checkerframework.checker.nullness.qual.NonNull;

public record ValidationResultMetaData(
    @NonNull
    @JsonProperty("validation_profile")
    XMLSyntaxType validationProfile,
    @NonNull
    @JsonProperty("validation_profile_version")
    String validation_profile_version
) {}
