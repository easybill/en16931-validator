package io.github.easybill;

import static io.restassured.RestAssured.given;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
class IndexControllerTest {

    @Test
    void testValidationEndpointWhenInvokedWithWrongMethod() {
        given().when().get("/validation").then().statusCode(405);
    }

    @Test
    void testValidationEndpointWhenInvokedWithAnEmptyPayload() {
        given().when().post("/validation").then().statusCode(415);
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "UBL/Allowance-example.xml",
            "UBL/base-creditnote-correction.xml",
            "UBL/base-example.xml",
            "UBL/base-negative-inv-correction.xml",
            "UBL/BIS3_Invoice_negativ.XML",
            "UBL/BIS3_Invoice_positive.XML",
            "UBL/guide-example1.xml",
            "UBL/guide-example2.xml",
            "UBL/guide-example3.xml",
            "UBL/issue116.xml",
            "UBL/sales-order-example.xml",
            "UBL/sample-discount-price.xml",
            "UBL/ubl-tc434-creditnote1.xml",
            "UBL/ubl-tc434-example1.xml",
            "UBL/ubl-tc434-example2.xml",
            "UBL/ubl-tc434-example3.xml",
            "UBL/ubl-tc434-example4.xml",
            "UBL/ubl-tc434-example6.xml",
            "UBL/ubl-tc434-example7.xml",
            "UBL/ubl-tc434-example8.xml",
            "UBL/ubl-tc434-example9.xml",
            "UBL/ubl-tc434-example10.xml",
            "UBL/vat-category-E.xml",
            "UBL/vat-category-O.xml",
            "UBL/Vat-category-S.xml",
            "UBL/vat-category-Z.xml",
        }
    )
    void testValidationEndpointWithValidUblDocuments(
        @NonNull String fixtureFileName
    ) throws IOException {
        given()
            .body(loadFixtureFileAsStream(fixtureFileName))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "CII/CII_business_example_01.xml",
            "CII/CII_business_example_02.xml",
            "CII/CII_business_example_Z.xml",
            "CII/CII_example1.xml",
            "CII/CII_example2.xml",
            "CII/CII_example3.xml",
            "CII/CII_example4.xml",
            "CII/CII_example5.xml",
            "CII/CII_example6.xml",
            "CII/CII_example7.xml",
            "CII/CII_example8.xml",
            "CII/CII_example9.xml",
            "CII/CII_ZUGFeRD_23_BASIC_Einfach.xml",
            "CII/CII_ZUGFeRD_23_BASIC_Rechnungskorrektur.xml",
            "CII/CII_ZUGFeRD_23_BASIC_Taxifahrt.xml",
            "CII/CII_ZUGFeRD_23_EN16931_Einfach.xml",
            "CII/CII_ZUGFeRD_23_EN16931_Einfach_DueDate.xml",
            "CII/CII_ZUGFeRD_23_EN16931_Elektron.xml",
            "CII/CII_ZUGFeRD_23_EN16931_Gutschrift.xml",
            "CII/CII_ZUGFeRD_23_EN16931_Rechnungskorrektur.xml",
            "CII/CII_ZUGFeRD_23_EN16931_Reisekostenabrechnung.xml",
            "CII/CII_ZUGFeRD_23_EN16931_SEPA_Prenotification.xml",
            "CII/CII_ZUGFeRD_23_EXTENDED_Fremdwaehrung.xml",
            "CII/CII_ZUGFeRD_23_EXTENDED_InnergemeinschLieferungMehrereBestellungen.xml",
            "CII/CII_ZUGFeRD_23_XRECHNUNG_Betriebskostenabrechnung.xml",
            "CII/CII_ZUGFeRD_23_XRECHNUNG_Einfach.xml",
            "CII/CII_ZUGFeRD_23_XRECHNUNG_Elektron.xml",
            "CII/CII_ZUGFeRD_23_XRECHNUNG_Reisekostenabrechnung.xml",
            "CII/XRechnung-O.xml",
        }
    )
    void testValidationEndpointWithValidCIIDocuments(
        @NonNull String fixtureFileName
    ) throws IOException {
        given()
            .body(loadFixtureFileAsStream(fixtureFileName))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "Invalid/CII_MissingExchangeDocumentContext.xml",
            "Invalid/Empty.xml",
            // Profile BASIC WL is not EN16931 conform. WL = Without Lines. EN16931 requires at least 1 line.
            "CII/CII_ZUGFeRD_23_BASIC-WL_Einfach.xml",
            // Profile MINIMUM is not EN16931 conform.
            "CII/CII_ZUGFeRD_Minimum.xml",
            "CII/CII_ZUGFeRD_23_MINIMUM_Buchungshilfe.xml",
            "CII/CII_ZUGFeRD_23_MINIMUM_Rechnung.xml",
            // Profile EXTENDED is EN16931 conform. However, those examples do have rounding issues. Which is valid
            // in EXTENDED Profile
            "CII/CII_ZUGFeRD_23_EXTENDED_Kostenrechnung.xml",
            "CII/CII_ZUGFeRD_23_EXTENDED_Projektabschlussrechnung.xml",
            "CII/CII_ZUGFeRD_23_EXTENDED_Rechnungskorrektur.xml",
            "CII/CII_ZUGFeRD_23_EXTENDED_Warenrechnung.xml",
        }
    )
    void testValidationEndpointWithInvalidPayload(
        @NonNull String fixtureFileName
    ) throws IOException {
        given()
            .body(loadFixtureFileAsStream(fixtureFileName))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(400);
    }

    @ParameterizedTest
    @MethodSource("providerValuesForDifferentEncodings")
    void testValidationEndpointWithDifferentEncodings(
        @NonNull String fixtureFileName
    ) throws IOException {
        given()
            .body(loadFixtureFileAsStream(fixtureFileName))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200);
    }

    static Stream<Arguments> providerValuesForDifferentEncodings() {
        return Stream.of(
            Arguments.of("UBL/base-example-utf16be.xml"),
            Arguments.of("UBL/base-example-utf16le.xml")
        );
    }

    InputStream loadFixtureFileAsStream(@NonNull String fixtureFileName)
        throws IOException {
        return Objects
            .requireNonNull(
                Thread
                    .currentThread()
                    .getContextClassLoader()
                    .getResource(fixtureFileName)
            )
            .openStream();
    }
}
