package io.github.easybill.Services;

import com.helger.commons.io.ByteArrayWrapper;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.SVRLMarshaller;
import com.helger.schematron.svrl.jaxb.FailedAssert;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import io.github.easybill.Contracts.IValidationService;
import io.github.easybill.Dtos.ValidationResult;
import io.github.easybill.Enums.XMLSyntaxType;
import io.github.easybill.Exceptions.InvalidXmlException;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.mozilla.universalchardet.UniversalDetector;

@Singleton
public final class ValidationService implements IValidationService {

    private final SchematronResourceSCH ciiSchematron;
    private final SchematronResourceSCH ublSchematron;

    ValidationService() {
        ciiSchematron =
            new SchematronResourceSCH(
                new ClassPathResource("EN16931-CII-1.3.12.sch")
            );

        ublSchematron =
            new SchematronResourceSCH(
                new ClassPathResource("EN16931-UBL-1.3.12.sch")
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

        if (isXmlInvalid(xml)) {
            throw new InvalidXmlException();
        }

        var xmlSyntaxType = determineXmlSyntax(xml)
            .orElseThrow(InvalidXmlException::new);

        var report = innerValidateSchematron(xmlSyntaxType, bytesFromSteam)
            .orElseThrow(RuntimeException::new);

        String reportXML = new SVRLMarshaller().getAsString(report);

        if (reportXML == null) {
            throw new RuntimeException("validation failed unexpectedly");
        }

        return new ValidationResult(
            report,
            reportXML,
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

    private List<FailedAssert> getErrorsFromSchematronOutput(
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
            .toList();
    }

    private List<FailedAssert> getWarningsFromSchematronOutput(
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

    private boolean isXmlInvalid(@NonNull String xml) {
        return xml.isBlank() || (!checkIfUblXml(xml) && !checkIfCiiXml(xml));
    }

    private Optional<XMLSyntaxType> determineXmlSyntax(@NonNull String xml) {
        if (checkIfCiiXml(xml)) {
            return Optional.of(XMLSyntaxType.CII);
        }

        if (checkIfUblXml(xml)) {
            return Optional.of(XMLSyntaxType.UBL);
        }

        return Optional.empty();
    }

    private boolean checkIfCiiXml(@NonNull CharSequence payload) {
        return Pattern
            .compile("[<:](CrossIndustryInvoice)")
            .matcher(payload)
            .find();
    }

    private boolean checkIfUblXml(@NonNull CharSequence payload) {
        return Pattern
            .compile("[<:](Invoice|CreditNote)")
            .matcher(payload)
            .find();
    }

    private Optional<SchematronOutputType> innerValidateSchematron(
        @NonNull XMLSyntaxType xmlSyntaxType,
        byte[] bytes
    ) throws Exception {
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
    }
}
