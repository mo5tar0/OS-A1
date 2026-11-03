/**
 * Counting semaphore built on Java monitors (synchronized / wait / notify),
 * adapted from the semaphore in Lab 4.
 *
 * Differences from the lab version:
 * - P() re-checks the condition in a while loop, so a waiting thread can never
 * proceed without a permit (needed because tryP() can time out).
 * - tryP(timeoutMillis) is the custom method required by the assignment:
 * it returns false if no permit became available within the time limit.
 */
public class Semaphore {
    protected int value;

    public Semaphore() {
        this(0);
    }

    public Semaphore(int initial) {
        if (initial < 0) {
            throw new IllegalArgumentException("Initial value must be >= 0");
        }
        value = initial;
    }

    /** wait / P: acquire one permit, blocking until one is available. */
    public synchronized void P() {
        while (value <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                // ignored, as in the lab version; keep waiting for a permit
            }
        }
        value--;
    }

    /** signal / V: release one permit and wake one waiting thread. */
    public synchronized void V() {
        value++;
        notify();
    }

    /**
     * Try to acquire a permit within the given time limit.
     *
     * @return true if a permit was acquired, false if the time ran out
     */
    public synchronized boolean tryP(long timeoutMillis) {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (value <= 0) {
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                return false;
            }
            try {
                wait(remaining);
            } catch (InterruptedException e) {
                // ignored; loop re-checks the value and the remaining time
            }
        }
        value--;
        return true;
    }
}
