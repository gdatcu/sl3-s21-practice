package ro.hapifyme.tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import ro.hapifyme.utils.RetryAnalyzer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Capitolul 5.3 - Metoda (1): Retry per test, cu @Test(retryAnalyzer = RetryAnalyzer.class).
 * Demonstratie deterministica: nu are nevoie de retea sau credentiale.
 *
 * Testul "pica" la prima incercare (simulam un glitch de retea) si trece la a doua.
 * In log apare: WARN RetryAnalyzer - Retry 1/2 pentru flakyOnFirstAttempt
 * In raport, prima incercare apare ca SKIPPED (retried), a doua ca PASSED.
 *
 * Metoda (2), globala, prin @Retryable + AnnotationTransformer: vezi SystemHealthTest.
 */
public class RetryDemoTest extends BaseTest {

    private static final AtomicInteger ATTEMPTS = new AtomicInteger();

    @Test(description = "Demo Retry: pica la prima incercare, trece la a doua",
            retryAnalyzer = RetryAnalyzer.class)
    public void flakyOnFirstAttempt() {
        int attempt = ATTEMPTS.incrementAndGet();
        logger.info("Incercarea nr. {}", attempt);
        Assert.assertTrue(attempt > 1, "Esec simulat la prima incercare (glitch de retea)");
    }
}
