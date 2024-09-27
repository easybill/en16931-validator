package io.github.easybill;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HealthControllerTest {

    @Test
    void testHealthEndpoint() throws IOException {
        given()
            .when()
            .get("/health")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }
}
