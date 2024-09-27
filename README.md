# en16931-validator
![Docker Image Version](https://img.shields.io/docker/v/easybill/en16931-validator)
[![Generic badge](https://img.shields.io/badge/License-MIT-blue.svg)]()

## Introduction
`en16931-validator` is a small service for validating XML against the official
EN16931 schematron rules. It exposes a validation endpoint which takes the
to be validated XML and returns the schematron report. The HTTP status code indicates if the
provided XML is valid (200) or has issues (400). UBL and CII is supported.

### Currently supported validation artifacts: [v1.3.12](https://github.com/ConnectingEurope/eInvoicing-EN16931/releases/tag/validation-1.3.12)

## Usage
This service was mainly designed with containerization in mind. So general idea is to use the following
docker image and make HTTP-Requests from the main application to the service for validation.

- modifying / creating docker-compose.yaml

> The service exposes a health endpoint which can be used to check if the service ready to be used at /health

> You can find a OpenAPI documentation after you started the service at /swagger
```yaml
  en16931-validator:
    image: 'easybill/en16931-validator:latest'
    ports:
      - '8081:8080'
    environment:
        JAVA_TOOL_OPTIONS: -Xmx512m
    healthcheck:
      test: curl --fail http://localhost:8081/health || exit 0
      interval: 10s
      retries: 6
```

- starting docker compose
```
docker compose up --detach --wait --wait-timeout 30
```

- Example of using this service (PHP)
```PHP
<?php

declare(strict_types=1);

final class EN16931Validator
{
    public function isValid(string $xml): ?bool
    {
        $httpClient = new Client();

        $response = $httpClient->request('POST', 'http://localhost:8081/validation', [
            RequestOptions::HEADERS => [
                'Content-Type' => 'application/xml',
            ],
            RequestOptions::BODY => $xml,
            RequestOptions::TIMEOUT => 10,
            RequestOptions::CONNECT_TIMEOUT => 10,
            RequestOptions::HTTP_ERRORS => false,
        ]);

        return 200 === $response->getStatusCode();
    }
}
```

- Example response in case the XML is invalid

> The `svrl:failed-assert` elements are relevant to be inspected. They contain the error message or warnings.
```xml
<?xml version="1.0" encoding="UTF-8"?>
<svrl:schematron-output xmlns:svrl="http://purl.oclc.org/dsdl/svrl" title="" schemaVersion="">
  <svrl:ns-prefix-in-attribute-values prefix="rsm" uri="urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100" />
  <svrl:ns-prefix-in-attribute-values prefix="ccts" uri="urn:un:unece:uncefact:documentation:standard:CoreComponentsTechnicalSpecification:2" />
  <svrl:ns-prefix-in-attribute-values prefix="udt" uri="urn:un:unece:uncefact:data:standard:UnqualifiedDataType:100" />
  <svrl:ns-prefix-in-attribute-values prefix="qdt" uri="urn:un:unece:uncefact:data:standard:QualifiedDataType:100" />
  <svrl:ns-prefix-in-attribute-values prefix="ram" uri="urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100" />
  <svrl:ns-prefix-in-attribute-values prefix="xs" uri="http://www.w3.org/2001/XMLSchema" />
  <svrl:active-pattern id="EN16931-CII-Model" name="EN16931-CII-Model" document="" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice" />
  <svrl:failed-assert id="BR-10" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]" test="rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress" flag="fatal">
    <svrl:text>[BR-10]-An Invoice shall contain the Buyer postal address (BG-8).</svrl:text>
  </svrl:failed-assert>
  <svrl:failed-assert id="BR-11" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]" test="normalize-space(rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement/ram:BuyerTradeParty/ram:PostalTradeAddress/ram:CountryID) != ''" flag="fatal">
    <svrl:text>[BR-11]-The Buyer postal address shall contain a Buyer country code (BT-55).</svrl:text>
  </svrl:failed-assert>
  <svrl:failed-assert id="BR-16" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]" test="//ram:IncludedSupplyChainTradeLineItem" flag="fatal">
    <svrl:text>[BR-16]-An Invoice shall have at least one Invoice line (BG-25).</svrl:text>
  </svrl:failed-assert>
  <svrl:failed-assert id="BR-CO-25" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]" test="(number(//ram:DuePayableAmount) > 0 and ((//ram:SpecifiedTradePaymentTerms/ram:DueDateDateTime) or (//ram:SpecifiedTradePaymentTerms/ram:Description))) or not(number(//ram:DuePayableAmount)>0)" flag="fatal">
    <svrl:text>[BR-CO-25]-In case the Amount due for payment (BT-115) is positive, either the Payment due date (BT-9) or the Payment terms (BT-20) shall be present.</svrl:text>
  </svrl:failed-assert>
  <svrl:fired-rule context="//ram:SellerTradeParty" />
  <svrl:fired-rule context="//ram:SpecifiedTaxRegistration/ram:ID[@schemeID='VA']" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery" />
  <svrl:fired-rule context="//ram:SpecifiedTradeSettlementHeaderMonetarySummation" />
  <svrl:failed-assert id="BR-12" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:SupplyChainTradeTransaction[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:ApplicableHeaderTradeSettlement[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]/*:SpecifiedTradeSettlementHeaderMonetarySummation[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]" test="(ram:LineTotalAmount)" flag="fatal">
    <svrl:text>[BR-12]-An Invoice shall have the Sum of Invoice line net amount (BT-106). </svrl:text>
  </svrl:failed-assert>
  <svrl:failed-assert id="BR-CO-10" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:SupplyChainTradeTransaction[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:ApplicableHeaderTradeSettlement[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]/*:SpecifiedTradeSettlementHeaderMonetarySummation[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]" test="xs:decimal(ram:LineTotalAmount) = round(xs:decimal(sum(../../ram:IncludedSupplyChainTradeLineItem/ram:SpecifiedLineTradeSettlement/ram:SpecifiedTradeSettlementLineMonetarySummation/ram:LineTotalAmount)) * xs:decimal(100)) div xs:decimal(100)" flag="fatal">
    <svrl:text>[BR-CO-10]-Sum of Invoice line net amount (BT-106) = Σ Invoice line net amount (BT-131).</svrl:text>
  </svrl:failed-assert>
  <svrl:failed-assert id="BR-CO-13" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:SupplyChainTradeTransaction[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:ApplicableHeaderTradeSettlement[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]/*:SpecifiedTradeSettlementHeaderMonetarySummation[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]" test="(xs:decimal(ram:TaxBasisTotalAmount) = round((xs:decimal(ram:LineTotalAmount) - xs:decimal(ram:AllowanceTotalAmount) + xs:decimal(ram:ChargeTotalAmount)) *10 * 10) div 100) or ((xs:decimal(ram:TaxBasisTotalAmount) = round((xs:decimal(ram:LineTotalAmount) - xs:decimal(ram:AllowanceTotalAmount)) *10 * 10) div 100) and not (ram:ChargeTotalAmount)) or ((xs:decimal(ram:TaxBasisTotalAmount) = round((xs:decimal(ram:LineTotalAmount) + xs:decimal(ram:ChargeTotalAmount)) *10 * 10) div 100) and not (ram:AllowanceTotalAmount)) or ((xs:decimal(ram:TaxBasisTotalAmount) = round((xs:decimal(ram:LineTotalAmount)) *10 * 10) div 100) and not (ram:ChargeTotalAmount) and not (ram:AllowanceTotalAmount))" flag="fatal">
    <svrl:text>[BR-CO-13]-Invoice total amount without VAT (BT-109) = Σ Invoice line net amount (BT-131) - Sum of allowances on document level (BT-107) + Sum of charges on document level (BT-108).</svrl:text>
  </svrl:failed-assert>
  <svrl:fired-rule context="//ram:SpecifiedTradeSettlementHeaderMonetarySummation/ram:TaxTotalAmount[@currencyID=/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:InvoiceCurrencyCode]" />
  <svrl:failed-assert id="BR-CO-14" location="/*:CrossIndustryInvoice[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:SupplyChainTradeTransaction[namespace-uri()='urn:un:unece:uncefact:data:standard:CrossIndustryInvoice:100'][1]/*:ApplicableHeaderTradeSettlement[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]/*:SpecifiedTradeSettlementHeaderMonetarySummation[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]/*:TaxTotalAmount[namespace-uri()='urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:100'][1]" test=". = (round(sum(/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:ApplicableTradeTax/ram:CalculatedAmount)*10*10)div 100)" flag="fatal">
    <svrl:text>[BR-CO-14]-Invoice total VAT amount (BT-110) = Σ VAT category tax amount (BT-117).</svrl:text>
  </svrl:failed-assert>
  <svrl:active-pattern id="EN16931-CII-Syntax" name="EN16931-CII-Syntax" document="" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:ExchangedDocumentContext" />
  <svrl:fired-rule context="//*[ends-with(name(), 'DocumentContextParameter')]" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'ID')]" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:ExchangedDocument" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'ID')]" />
  <svrl:fired-rule context="//ram:TypeCode" />
  <svrl:fired-rule context="//udt:DateTimeString[@format = '102']" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeAgreement" />
  <svrl:fired-rule context="//ram:PostalTradeAddress" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'ID')]" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'ID')]" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'ID')]" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeDelivery" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement" />
  <svrl:fired-rule context="/rsm:CrossIndustryInvoice/rsm:SupplyChainTradeTransaction/ram:ApplicableHeaderTradeSettlement/ram:SpecifiedTradeSettlementHeaderMonetarySummation" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'Amount') and not (self::ram:TaxTotalAmount)]" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'Amount') and not (self::ram:TaxTotalAmount)]" />
  <svrl:fired-rule context="//ram:*[ends-with(name(), 'Amount') and not (self::ram:TaxTotalAmount)]" />
  <svrl:active-pattern id="EN16931-Codes" name="EN16931-Codes" document="" />
  <svrl:fired-rule context="rsm:ExchangedDocument/ram:TypeCode" />
  <svrl:fired-rule context="ram:CountryID" />
  <svrl:fired-rule context="ram:InvoiceCurrencyCode" />
  <svrl:fired-rule context="ram:TaxTotalAmount[@currencyID]" />
</svrl:schematron-output>
```

## Issues & Contribution
Feel free to create pull-requests or issues if you have trouble with this service or any related resources. 