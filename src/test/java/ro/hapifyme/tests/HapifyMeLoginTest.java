package ro.hapifyme.tests;

import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Capitolul 2.3: hapifyMe Login cu Allure
 * + Capitolul 2.9: solutia exercitiului (pasul "Verificare timp de raspuns").
 */
@Epic("hapifyMe Core")
@Feature("Authentication Module")
public class HapifyMeLoginTest extends BaseTest {

    @Test(priority = 1, description = "Validare Login API cu user corect")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Acest test verifica endpoint-ul POST /auth/login. Se asteapta un token JWT in raspuns.")
    @Story("JIRA-101: Ca utilizator, vreau sa ma pot loga.")
    public void testValidLogin() {
        // Logica testului este sparta in pasi
        String username = System.getenv("HAPIFY_USER");
        String password = System.getenv("HAPIFY_PASS");

//        String username = "test_automat";
//        String password = "Password@123";
//        mvn clean test -Dtest=ro.hapifyme.tests.HapifyMeLoginTest

        // Pas 1: Actiunea
        Response response = performLogin(username, password);

        // Exercitiul 2.8: noul pas de verificare a performantei
        verifyResponseTime(response);

        // Pas 2: Verificarea
        verifyLoginSuccess(response);
    }

    // Adnotarea @Step face ca aceasta metoda sa apara in raport ca o actiune distincta
    // {0} este inlocuit cu valoarea primului parametru (user). Parola NU apare in raport!
    @Step("Trimite cerere POST login pentru user: {0}")
    public Response performLogin(String user, String pass) {
        String endpoint = BASE_URL + "/auth/login";
        String payload = "{\"log_email\": \"" + user + "\", \"log_password\": \"" + pass + "\"}";

        // Logam si pentru debug (vezi Cap 1)
        logger.info("Se executa pasul Allure: performLogin");

        // Atasam request-ul in raport (pentru debugging vizual) - cu parola mascata!
        String safePayload = (pass == null || pass.isEmpty()) ? payload : payload.replace(pass, "*****");
        attachTextToReport("Request Payload", safePayload);

        return RestAssured.given()
                .contentType("application/json")
                .body(payload)
                .post(endpoint);
    }

    @Step("Verificare timp de raspuns (trebuie sa fie sub 5s)")
    public void verifyResponseTime(Response response) {
        long timeInMs = response.time();
        logger.info("Timpul de raspuns a fost: " + timeInMs + " ms");

        // Atasam valoarea exacta in raport pentru claritate
        attachTextToReport("Timp masurat", timeInMs + " ms");

        Assert.assertTrue(timeInMs < 5000,
                "Performanta slaba! Timpul de raspuns (" + timeInMs + "ms) a depasit limita de 2000ms.");
    }

    @Step("Valideaza status 200 si prezenta token-ului")
    public void verifyLoginSuccess(Response response) {
        // Atasam raspunsul complet in raport
        attachTextToReport("API Response", response.getBody().asString());

        Assert.assertEquals(response.getStatusCode(), 200, "Status code incorect!");
        String token = response.jsonPath().getString("data.token");

        Assert.assertNotNull(token, "Token-ul nu a fost returnat!");
        logger.info("Validare reusita. Token primit (" + token.length() + " caractere).");
    }

    // Metoda ajutatoare pentru a atasa text (JSON, Logs) in raportul Allure
    @Attachment(value = "{0}", type = "text/plain")
    public String attachTextToReport(String attachmentName, String message) {
        return message; // Allure ia valoarea returnata si o pune in raport
    }
}
