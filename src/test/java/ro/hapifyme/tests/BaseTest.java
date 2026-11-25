package ro.hapifyme.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import ro.hapifyme.utils.TestContext;

import java.lang.reflect.Method;

/**
 * BaseTest unificat:
 * - Capitolul 1.4: Logger Log4j pentru clasa curenta
 * - Capitolul 3.8, Exercitiul 2: URL-ul de baza din variabila HAPIFY_BASE_URL (cu fallback local)
 * - Capitolul 5.8, Solutie Exercitiul 1: ID-ul thread-ului + curatarea ThreadLocal dupa fiecare test
 */
public class BaseTest {

    /** URL-ul implicit, folosit cand HAPIFY_BASE_URL lipseste (rulari locale). */
    public static final String DEFAULT_BASE_URL = "https://apps.qualiadept.eu/hapifyme/api/v1";

    /** Capitolul 3, Exercitiul 2: in GitHub Actions vine din ${{ vars.HAPIFY_BASE_URL }}. */
    protected static final String BASE_URL = resolveBaseUrl();

    // Initializam Logger-ul (Log4j) pentru clasa curenta
    protected final Logger logger = LogManager.getLogger(getClass());

    private static String resolveBaseUrl() {
        String baseUrl = System.getenv("HAPIFY_BASE_URL");
        // Daca variabila nu e definita in GitHub, ${{ vars... }} devine string gol, nu null
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = DEFAULT_BASE_URL; // fallback local
        }
        return baseUrl;
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Method method) {
        // Logam informatiile esentiale la inceputul fiecarui test
        logger.info("========================================");
        logger.info("Incepere Test: " + method.getName());
        // Adaugam ID-ul thread-ului pentru a putea depana executia paralela
        logger.info("Running on Thread ID: " + Thread.currentThread().threadId());
        logger.info("Base URL: " + BASE_URL);
        logger.info("========================================");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // Critic pentru paralelizare: Curatam datele din ThreadLocal
        // Daca nu facem asta, urmatorul test care refoloseste acest thread ar putea vedea date vechi
        TestContext.clear();
        logger.debug("Context curatat pentru Thread ID: " + Thread.currentThread().threadId());
    }
}
