package ro.hapifyme.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Capitolul 5.3 - Implementare RetryAnalyzer.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger =
            LogManager.getLogger(RetryAnalyzer.class);
    private int count = 0;
    private static final int MAX_RETRY_COUNT = 2; // Mai incercam de maxim 2 ori

    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY_COUNT) {
            count++;
            logger.warn("Retry {}/{} pentru {}",
                    count, MAX_RETRY_COUNT, result.getName());
            return true; // Spune TestNG sa re-execute testul
        }
        return false;
    }
}
