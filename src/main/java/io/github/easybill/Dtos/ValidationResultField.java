package io.github.easybill.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helger.schematron.svrl.jaxb.FailedAssert;
import com.helger.schematron.svrl.jaxb.Text;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

enum Severity {
    FATAL,
    WARNING,
}

public record ValidationResultField(
    @JsonProperty("rule_id") @NonNull String id,
    @JsonProperty("rule_location") @NonNull String location,
    @JsonProperty("rule_severity") @NonNull Severity severity,
    @JsonProperty("rule_messages") @NonNull List<@NonNull String> messages
) {
    public ValidationResultField {
        messages = Collections.unmodifiableList(messages);
    }

    public static ValidationResultField fromFailedAssert(
        @NonNull FailedAssert failedAssert
    ) {
        var messages = failedAssert
            .getDiagnosticReferenceOrPropertyReferenceOrText()
            .stream()
            .filter(element -> element instanceof Text)
            .map(element ->
                ((Text) element).getContent()
                    .stream()
                    .filter(innerElement -> innerElement instanceof String)
                    .map(innerElement -> (String) innerElement)
                    .toList()
            )
            .flatMap(List::stream)
            .toList();

        return new ValidationResultField(
            Objects.requireNonNullElse(failedAssert.getId(), ""),
            Objects.requireNonNullElse(failedAssert.getLocation(), ""),
            Objects.equals(failedAssert.getFlag(), "fatal")
                ? Severity.FATAL
                : Severity.WARNING,
            messages
        );
    }
}
