package io.github.easybill.Services;

import io.github.easybill.Enums.XMLSyntaxType;
import java.util.Optional;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class XMLSyntaxGuesser {

    public static Optional<XMLSyntaxType> tryGuessSyntax(@NonNull String xml) {
        if (checkIfCiiXml(xml)) {
            return Optional.of(XMLSyntaxType.CII);
        }

        if (checkIfUblXml(xml)) {
            return Optional.of(XMLSyntaxType.UBL);
        }

        return Optional.empty();
    }

    private static boolean checkIfCiiXml(@NonNull CharSequence payload) {
        return Pattern
            .compile("[<:](CrossIndustryInvoice)")
            .matcher(payload)
            .find();
    }

    private static boolean checkIfUblXml(@NonNull CharSequence payload) {
        return Pattern
            .compile("[<:](Invoice|CreditNote)")
            .matcher(payload)
            .find();
    }
}
