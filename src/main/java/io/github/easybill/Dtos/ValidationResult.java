package io.github.easybill.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public record ValidationResult(
    @NonNull ValidationResultMetaData meta,
    @NonNull List<@NonNull ValidationResultField> errors,
    @NonNull List<@NonNull ValidationResultField> warnings
) {
    public ValidationResult {
        errors = Collections.unmodifiableList(errors);
        warnings = Collections.unmodifiableList(warnings);
    }

    @JsonProperty("is_valid")
    public boolean isValid() {
        return errors.isEmpty();
    }
}
