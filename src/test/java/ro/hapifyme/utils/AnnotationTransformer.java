package ro.hapifyme.utils;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Capitolul 5.3 - ataseaza RetryAnalyzer DOAR testelor marcate cu @Retryable.
 * Se activeaza din testng.xml (sectiunea listeners).
 */
public class AnnotationTransformer implements IAnnotationTransformer {
    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        // Retry DOAR pentru testele marcate cu @Retryable (opt-in)
        if (testMethod != null
                && testMethod.isAnnotationPresent(Retryable.class)) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
