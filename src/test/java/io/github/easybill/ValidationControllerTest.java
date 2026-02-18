package io.github.easybill;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.github.easybill.Enums.XMLSyntaxType;
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
class ValidationControllerTest {

    @Test
    void testValidationEndpointWhenInvokedWithWrongMethod() {
        given().when().get("/validation").then().statusCode(405);
    }

    @Test
    void testValidationEndpointWhenInvokedWithAnEmptyPayload() {
        given().when().post("/validation").then().statusCode(415);
    }

    @Test
    void testValidationEndpointWithEmptyPayload() throws IOException {
        given()
            .body(loadFixtureFileAsStream("Invalid/Invalid.xml"))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(422);
    }

    @Test
    void testValidationEndpointWithPayloadWithMissingNamespaces()
        throws IOException {
        given()
            .body(loadFixtureFileAsStream("Invalid/CII_missing_namespace.xml"))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(422);
    }

    @Test
    void testValidationEndpointWithPayloadIncludingBOM() throws IOException {
        given()
            .body(loadFixtureFileAsStream("CII/EN16931_Einfach_BOM.xml"))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("is_valid", equalTo(true))
            .body("errors", empty());
    }

    @Test
    // The XML tag (i. e. <?xml) is optional. So we should be able to parse the xml even if it is missing the tag
    void testValidationEndpointWithPayloadMissingTheXmlTag()
        throws IOException {
        given()
            .body(loadFixtureFileAsStream("UBL/xrechnung-missing-xml-tag.xml"))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("is_valid", equalTo(true))
            .body("errors", empty());
    }

    @Test
    void testValidationEndpointWithPayloadIncludingCharsInProlog()
        throws IOException {
        given()
            .body(loadFixtureFileAsStream("Invalid/EN16931_Einfach_BOM.xml"))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(strings = { "CII/CII_empty_tags.xml" })
    void testDocumentWithEmptyTags(@NonNull String fixtureFileName)
        throws IOException {
        given()
            .body(loadFixtureFileAsStream(fixtureFileName))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("is_valid", equalTo(false))
            .body(
                "meta.validation_profile",
                equalTo(XMLSyntaxType.CII.toString())
            )
            .body("errors", not(empty()));
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
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("is_valid", equalTo(true))
            .body(
                "meta.validation_profile",
                equalTo(XMLSyntaxType.UBL.toString())
            )
            .body("errors", empty());
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
            "CII/CII_ZUGFeRD_23_EXTENDED_Rechnungskorrektur.xml",
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
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("is_valid", equalTo(true))
            .body(
                "meta.validation_profile",
                equalTo(XMLSyntaxType.CII.toString())
            )
            .body("errors", empty());
    }

    static Stream<Arguments> providerValuesValidationEndpointWithInvalidPayload() {
        return Stream.of(
            Arguments.of("Invalid/CII_MissingExchangeDocumentContext.xml"),
            //Arguments.of("Invalid/Empty.xml"),
            // Uses HRK as currency which is no longer supported in EN16931
            Arguments.of("UBL/sample-discount-price.xml"),
            // Profile BASIC WL is not EN16931 conform. WL = Without Lines. EN16931 requires at least 1 line.
            Arguments.of("CII/CII_ZUGFeRD_23_BASIC-WL_Einfach.xml"),
            // Profile MINIMUM is not EN16931 conform.
            Arguments.of("CII/CII_ZUGFeRD_Minimum.xml"),
            Arguments.of("CII/CII_ZUGFeRD_23_MINIMUM_Buchungshilfe.xml"),
            Arguments.of("CII/CII_ZUGFeRD_23_MINIMUM_Rechnung.xml"),
            // Profile EXTENDED is EN16931 conform. However, those examples do have rounding issues. Which is valid
            // in EXTENDED Profile
            Arguments.of("CII/CII_ZUGFeRD_23_EXTENDED_Kostenrechnung.xml"),
            Arguments.of("CII/CII_ZUGFeRD_23_EXTENDED_Warenrechnung.xml"),
            Arguments.of("CII/XRechnung-O.xml"),
            Arguments.of(
                "CII/CII_ZUGFeRD_23_EXTENDED_Projektabschlussrechnung.xml"
            )
        );
    }

    @ParameterizedTest
    @MethodSource("providerValuesValidationEndpointWithInvalidPayload")
    void testValidationEndpointWithInvalidPayload(
        @NonNull String fixtureFileName
    ) throws IOException {
        given()
            .body(loadFixtureFileAsStream(fixtureFileName))
            .contentType(ContentType.XML)
            .when()
            .post("/validation")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("is_valid", equalTo(false))
            .body("errors", not(empty()));
    }

    static Stream<Arguments> providerValuesForDifferentEncodings() {
        return Stream.of(
            Arguments.of("UBL/base-example-utf16be.xml"),
            Arguments.of("UBL/base-example-utf16le.xml")
        );
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
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("is_valid", equalTo(true))
            .body("errors", empty());
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
