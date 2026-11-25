package ro.hapifyme.tests;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * Capitolul 5.7, Exercitiul 2 - BONUS: aceeasi logica, dar cu un simplu "static String"
 * in loc de ThreadLocal. Thread-urile isi suprascriu token-ul unul altuia.
 *
 * Testul este DEZACTIVAT implicit (enabled = false). Pentru experiment:
 *   1. schimbati enabled = false in enabled = true
 *   2. mvn test -Dtest=StaticTokenBonusTest
 *   3. comparati in log prefixul token-ului "primit" vs. "folosit" pe fiecare thread.
 */
public class StaticTokenBonusTest extends BaseTest {

    // GRESIT intentionat: un singur "sertar" pentru toate thread-urile
    private static String currentToken;

    @Test(invocationCount = 4, threadPoolSize = 4, enabled = false)
    public void testUserFlowWithStaticToken() {
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

        currentToken = token; // alt thread poate suprascrie valoarea chiar acum
        logger.info("Thread {} a PRIMIT token-ul {}...",
                Thread.currentThread().threadId(), prefix(token));

        checkUserProfile();
    }

    private void checkUserProfile() {
        String used = currentToken;
        logger.info("Thread {} FOLOSESTE token-ul {}...",
                Thread.currentThread().threadId(), prefix(used));
        given()
                .baseUri(BASE_URL)
                .auth().oauth2(used)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200);
    }

    private static String prefix(String token) {
        return token == null ? "null" : token.substring(0, Math.min(10, token.length()));
    }
}
