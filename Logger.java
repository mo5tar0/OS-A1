/**
 * Thread-safe console output so lines from different threads never interleave.
 * Several lines passed in one call are printed together, with no blank lines.
 */
public final class Logger {
    private static final Object LOCK = new Object();

    private Logger() {
    }

    public static void log(String... lines) {
        synchronized (LOCK) {
            for (String line : lines) {
                System.out.println(line);
            }
        }
    }
}