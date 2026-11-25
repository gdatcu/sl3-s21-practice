package ro.hapifyme.utils;

/**
 * Capitolul 5.2 - Implementare TestContext robusta (ThreadLocal).
 * Fiecare thread are propriul "sertar" pentru token si userId.
 */
public class TestContext {
    // Containerul magic: Fiecare thread are propriul sertar pentru Token
    private static final ThreadLocal<String> authToken = new ThreadLocal<>();
    private static final ThreadLocal<Integer> userId = new ThreadLocal<>();

    private TestContext() {
    }

    // Setters
    public static void setToken(String token) {
        authToken.set(token);
    }

    public static void setUserId(int id) {
        userId.set(id);
    }

    // Getters
    public static String getToken() {
        return authToken.get();
    }

    public static int getUserId() {
        Integer id = userId.get();
        if (id == null) { // evitam NPE la unboxing
            throw new IllegalStateException("userId nu a fost setat");
        }
        return id;
    }

    // Cleanup (Important!)
    public static void clear() {
        authToken.remove();
        userId.remove();
    }
}
