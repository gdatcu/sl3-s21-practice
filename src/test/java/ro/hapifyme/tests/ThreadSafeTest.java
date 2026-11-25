package ro.hapifyme.tests;

import org.testng.annotations.Test;
import ro.hapifyme.utils.TestContext;

import static io.restassured.RestAssured.given;

/**
 * Capitolul 5.8, Solutie Exercitiul 2: ThreadSafeTest cu login real.
 * Observati cum checkUserProfile() nu primeste token-ul ca parametru,
 * ci il ia "din aer" (ThreadLocal).
 *
 * BASE_URL vine din BaseTest (in material este declarat local in clasa).
 */
public class ThreadSafeTest extends BaseTest {

    // 4 rulari simultane pe 4 thread-uri (fara sa modificam testng.xml)
    @Test(invocationCount = 4, threadPoolSize = 4)
    public void testUserFlow() {
        // 1. Login REAL - fiecare thread primeste propriul token
        String payload = String.format(
                "{\"log_email\": \"%s\", \"log_password\": \"%s\"}",
                System.getenv("HAPIFY_USER"), System.getenv("HAPIFY_PASS"));

        String token = given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(payload)
                .post("/auth/login")
                .then().statusCode(200)
                .extract().path("data.token");

        // 2. SALVAM token-ul in "sertarul" thread-ului curent
        TestContext.setToken(token);
        logger.info("Thread {} a primit un token ({} caractere)",
                Thread.currentThread().threadId(), token.length());

        // 3. Apelam metode ulterioare FARA sa pasam token-ul manual
        checkUserProfile();
    }

    // Aceasta metoda poate fi intr-o clasa separata (ex: API Actions)
    private void checkUserProfile() {
        given()
                .baseUri(BASE_URL)
                // RECUPERAM token-ul direct din ThreadLocal
                .auth().oauth2(TestContext.getToken()) // header Bearer
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200);
    }
}
