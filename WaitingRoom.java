
public class WaitingRoom {
    private final int capacity;
    private final Patient[] store;
    private int inPtr = 0;
    private int outPtr = 0;

    private final Semaphore spaces;
    private final Semaphore elements = new Semaphore(0);
    private final Semaphore mutex = new Semaphore(1);

    private volatile boolean closed = false;

    public WaitingRoom(int capacity) {
        this.capacity = capacity;
        this.store = new Patient[capacity];
        this.spaces = new Semaphore(capacity);
    }

    public void arriveAndEnter(Patient p) {
        enter(p, true);
    }

    public void enter(Patient p) {
        enter(p, false);
    }

    private void enter(Patient p, boolean arriving) {
        if (!spaces.tryP(0)) {
            if (arriving) {
                Logger.log(
                    p + " arrived.",
                    "The waiting room is full.",
                    p + " is waiting outside for an available seat."
                );
            } else {
                Logger.log(
                    "The waiting room is full.",
                    p + " is waiting outside for an available seat."
                );
            }

            spaces.P();
        } else if (arriving) {
            mutex.P();
            store[inPtr] = p;
            inPtr = (inPtr + 1) % capacity;

            Logger.log(
                p + " arrived.",
                p + " entered the waiting room."
            );

            mutex.V();
            elements.V();
            return;
        }

        mutex.P();
        store[inPtr] = p;
        inPtr = (inPtr + 1) % capacity;

        Logger.log(p + " entered the waiting room.");

        mutex.V();
        elements.V();
    }

    public Patient take(Doctor d) {
        elements.P();
        mutex.P();

        if (closed) {
            mutex.V();
            return null;
        }

        Patient p = store[outPtr];
        store[outPtr] = null;
        outPtr = (outPtr + 1) % capacity;

        Logger.log(d + " is assigned to " + p + ".");

        mutex.V();
        spaces.V();

        return p;
    }

    public void close(int doctorCount) {
        closed = true;

        for (int i = 0; i < doctorCount; i++) {
            elements.V();
        }
    }
}
