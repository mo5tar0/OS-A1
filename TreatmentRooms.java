
public class TreatmentRooms {
    public static final int ROOM_COUNT = 2;

    private final Semaphore rooms = new Semaphore(ROOM_COUNT);

    public boolean tryAcquire(long timeoutMillis) {
        return rooms.tryP(timeoutMillis);
    }

    public void release() {
        rooms.V();
    }
}
