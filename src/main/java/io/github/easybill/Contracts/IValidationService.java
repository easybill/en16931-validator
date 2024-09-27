package io.github.easybill.Contracts;

import io.github.easybill.Dtos.ValidationResult;
import java.io.InputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface IValidationService {
    @NonNull
    ValidationResult validateXml(@NonNull InputStream inputStream)
        throws Exception;

    boolean isLoadedSchematronValid();
}
