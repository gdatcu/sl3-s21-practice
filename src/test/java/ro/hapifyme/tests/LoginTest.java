package ro.hapifyme.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Capitolul 1.4, Pasul 2: Utilizare Log4j in LoginTest.
 * Exercitiile 1.1 si 1.2 se fac in src/test/resources/log4j2.xml.
 */
public class LoginTest extends BaseTest {

    @Test
    public void testLoginWithLog4j() {
        String endpoint = BASE_URL + "/auth/login";
        // Credentialele vin din variabile de mediu - nu le scriem niciodata in cod!
        String username = System.getenv("HAPIFY_USER");
        String password = System.getenv("HAPIFY_PASS");
        String payload = String.format("{\"log_email\": \"%s\", \"log_password\": \"%s\"}", username, password);

        logger.info("Pas 1: Pregatire request catre endpoint-ul: " + endpoint);
        logger.debug("Payload trimis pentru user: " + username); // Parola NU se logheaza! Apare doar daca setam Level=DEBUG in xml

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(payload)
                .post(endpoint);

        logger.info("Pas 2: Request trimis. Status primit: " + response.getStatusCode());

        if (response.getStatusCode() != 200) {
            logger.error("Login esuat! Body raspuns: " + response.getBody().asString());
        } else {
            logger.info("Login reusit. Token extras.");
        }

        Assert.assertEquals(response.getStatusCode(), 200);
    }
}
