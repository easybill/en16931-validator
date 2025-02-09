package io.github.easybill.Services;

import com.helger.commons.io.ByteArrayWrapper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.jaxb.FailedAssert;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import io.github.easybill.Contracts.IApplicationConfig;
import io.github.easybill.Contracts.IValidationService;
import io.github.easybill.Dtos.ValidationResult;
import io.github.easybill.Dtos.ValidationResultField;
import io.github.easybill.Dtos.ValidationResultMetaData;
import io.github.easybill.Enums.XMLSyntaxType;
import io.github.easybill.Exceptions.InvalidXmlException;
import io.github.easybill.Exceptions.ParsingException;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.sf.saxon.type.ValidationException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mozilla.universalchardet.UniversalDetector;

@Singleton
public final class ValidationService implements IValidationService {

    private final String artefactsVersion;
    private final SchematronResourceSCH ciiSchematron;
    private final SchematronResourceSCH ublSchematron;

    ValidationService(IApplicationConfig config) {
        artefactsVersion = config.artefacts().version();

        ciiSchematron =
            new SchematronResourceSCH(
                new ClassPathResource(
                    String.format("EN16931-CII-%s.sch", artefactsVersion)
                )
            );

        ublSchematron =
            new SchematronResourceSCH(
                new ClassPathResource(
                    String.format("EN16931-UBL-%s.sch", artefactsVersion)
                )
            );

        if (!ciiSchematron.isValidSchematron()) {
            throw new RuntimeException("Schematron validation failed for CII");
        }

        if (!ublSchematron.isValidSchematron()) {
            throw new RuntimeException("Schematron validation failed for UBL");
        }
    }

    @Override
    public @NonNull ValidationResult validateXml(
        @NonNull InputStream inputStream
    ) throws Exception {
        var bytesFromSteam = inputStream.readAllBytes();

        var charset = determineCharsetForXmlPayload(bytesFromSteam);

        var xml = new String(bytesFromSteam, charset);

        if (xml.isBlank()) {
            throw new InvalidXmlException();
        }

        xml = XMLSanitizer.sanitize(xml, charset);

        var xmlSyntaxType = XMLSyntaxGuesser
            .tryGuessSyntax(xml)
            .orElseThrow(InvalidXmlException::new);

        var report = innerValidateSchematron(
            xmlSyntaxType,
            xml.getBytes(charset)
        )
            .orElseThrow(RuntimeException::new);

        return new ValidationResult(
            new ValidationResultMetaData(xmlSyntaxType, artefactsVersion),
            getErrorsFromSchematronOutput(report),
            getWarningsFromSchematronOutput(report)
        );
    }

    @Override
    public boolean isLoadedSchematronValid() {
        return (
            ciiSchematron.isValidSchematron() &&
            ublSchematron.isValidSchematron()
        );
    }

    private List<@NonNull ValidationResultField> getErrorsFromSchematronOutput(
        @NonNull SchematronOutputType outputType
    ) {
        return outputType
            .getActivePatternAndFiredRuleAndFailedAssert()
            .stream()
            .filter(element -> element instanceof FailedAssert)
            .filter(element ->
                Objects.equals(((FailedAssert) element).getFlag(), "fatal")
            )
            .map(element -> (FailedAssert) element)
            .map(ValidationResultField::fromFailedAssert)
            .toList();
    }

    private List<@NonNull ValidationResultField> getWarningsFromSchematronOutput(
        @NonNull SchematronOutputType outputType
    ) {
        return outputType
            .getActivePatternAndFiredRuleAndFailedAssert()
            .stream()
            .filter(element -> element instanceof FailedAssert)
            .filter(element ->
                Objects.equals(((FailedAssert) element).getFlag(), "warning")
            )
            .map(element -> (FailedAssert) element)
            .map(ValidationResultField::fromFailedAssert)
            .toList();
    }

    private Charset determineCharsetForXmlPayload(byte[] bytes)
        throws InvalidXmlException {
        UniversalDetector detector = new UniversalDetector();

        detector.handleData(bytes, 0, bytes.length);
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();

        detector.reset();

        if (encoding != null) {
            return Charset.forName(encoding);
        }

        throw new InvalidXmlException();
    }

    private Optional<SchematronOutputType> innerValidateSchematron(
        @NonNull XMLSyntaxType xmlSyntaxType,
        byte[] bytes
    ) throws Exception {
        try {
            return switch (xmlSyntaxType) {
                case CII -> Optional.ofNullable(
                    ciiSchematron.applySchematronValidationToSVRL(
                        new ByteArrayWrapper(bytes, false)
                    )
                );
                case UBL -> Optional.ofNullable(
                    ublSchematron.applySchematronValidationToSVRL(
                        new ByteArrayWrapper(bytes, false)
                    )
                );
            };
        } catch (IllegalArgumentException exception) {
            throw new ParsingException(exception);
        } catch (ValidationException exception) {
            throw new InvalidXmlException(exception);
        }
    }
}
