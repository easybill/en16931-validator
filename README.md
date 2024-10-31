# en16931-validator
![Docker Image Version](https://img.shields.io/docker/v/easybill/en16931-validator)
[![Generic badge](https://img.shields.io/badge/License-MIT-blue.svg)]()

## Introduction
`en16931-validator` is a small service for validating XML against the official
EN16931 schematron rules. It exposes a validation endpoint which takes the
to be validated XML and returns a JSON payload which contains possible warnings or errors. The HTTP status code indicates if the
provided XML is valid (200) or has issues (400). UBL and CII is supported.

### Currently supported validation artifacts: [v1.3.13](https://github.com/ConnectingEurope/eInvoicing-EN16931/releases/tag/validation-1.3.13)

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
                'Content-Type' => 'application/json',
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

```JSON
{
  "meta": {
    "validation_profile": "UBL",
    "validation_profile_version": "1.3.13"
  },
  "errors": [
    {
      "rule_id": "BR-03",
      "rule_location": "/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]",
      "rule_severity": "FATAL",
      "rule_messages": [
        "[BR-03]-An Invoice shall have an Invoice issue date (BT-2)."
      ]
    }
  ],
  "warnings": [],
  "is_valid": false
}
```

## Insights
You may enable bug reporting via Bugsnag by supplying the env-variable `BUGSNAG_API_KEY`.
```yaml
  en16931-validator:
    image: 'easybill/en16931-validator:latest'
    ports:
      - '8081:8080'
    environment:
        JAVA_TOOL_OPTIONS: -Xmx512m
        BUGSNAG_API_KEY: <YOUR_API_KEY>
    healthcheck:
      test: curl --fail http://localhost:8081/health || exit 0
      interval: 10s
      retries: 6
```

## Issues & Contribution
Feel free to create pull-requests or issues if you have trouble with this service or any related resources. 

## Roadmap
TBA