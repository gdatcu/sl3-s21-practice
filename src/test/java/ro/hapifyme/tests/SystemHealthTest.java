package ro.hapifyme.tests;

import org.testng.annotations.Test;
import ro.hapifyme.utils.Retryable;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Capitolul 5.1: SystemHealthTest (folosit pentru demonstrarea paralelizarii)
 * + Capitolul 5.8, Solutie Exercitiul 3: @Retryable pe un GET idempotent.
 *
 * Exercitiul 3 (fortam testul sa pice ca sa vedem reincercarile in log):
 *   mvn test -Dhealth.expected=degraded
 */
public class SystemHealthTest extends BaseTest {

    /** Implicit "healthy"; pentru Exercitiul 3 rulati cu -Dhealth.expected=degraded. */
    private static final String EXPECTED_STATUS = System.getProperty("health.expected", "healthy");

    @Test(description = "Verifica starea de sanatate a sistemului (Health Check)")
    @Retryable // GET idempotent - sigur de reincercat
    public void testSystemHealth() {

        given()
                .baseUri(BASE_URL)
                // Logam doar daca testul esueaza pentru a nu aglomera consola in executia paralela
                .log().ifValidationFails()
                .when()
                .get("/health")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.status", equalTo(EXPECTED_STATUS));

        // Adaugam un mic delay artificial doar pentru a observa paralelizarea in consola
        // (In productie nu facem asta, dar aici ajuta didactic)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // pastram semnalul de intrerupere
        }

        logger.info("System Health Check finalizat cu succes pe thread-ul: " + Thread.currentThread().threadId());
    }
}
