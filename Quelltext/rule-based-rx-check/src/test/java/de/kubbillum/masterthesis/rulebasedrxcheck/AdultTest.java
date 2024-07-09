package de.kubbillum.masterthesis.rulebasedrxcheck;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class AdultTest {

    @LocalServerPort
    private int port;

    @Test
    public void testEvaluateTrafficViolation() {
        RestAssured.port = port;
        given()
               .body("{\n" +
                     "    \"age\": 20" +                
                     "}")
               .contentType(ContentType.JSON)
          .when()
               .post("/adult")
          .then()
             .statusCode(200)
               .body("adult", is(Boolean.TRUE));
    }
}