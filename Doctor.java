
import java.util.Random;

public class Doctor extends Thread {
    private static final long ROOM_TIMEOUT_MS = 3000;
    private static final int MIN_TREAT_MS = 4000;
    private static final int MAX_TREAT_MS = 7000;

    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRooms treatmentRooms;
    private final Semaphore treatedCount;
    private final Random random = new Random();

    public Doctor(int id, WaitingRoom waitingRoom,
                  TreatmentRooms treatmentRooms, Semaphore treatedCount) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRooms = treatmentRooms;
        this.treatedCount = treatedCount;
    }

    @Override
    public void run() {
        while (true) {
            Patient patient = waitingRoom.take(this);

            if (patient == null) {
                return;
            }

            Logger.log(this + " is trying to acquire a treatment room.");

            if (!treatmentRooms.tryAcquire(ROOM_TIMEOUT_MS)) {
                Logger.log(
                    this + " could not acquire a treatment room.",
                    this + " is sending " + patient
                        + " back to the waiting room."
                );

                waitingRoom.enter(patient);
                Logger.log(this + " is now available.");
                continue;
            }

            Logger.log(
                this + " acquired a treatment room.",
                this + " begins treating " + patient + "."
            );

            boolean completed = treat();

            if (completed) {
                Logger.log(this + " finished treating " + patient + ".");
            }

            treatmentRooms.release();
            Logger.log(this + " released the treatment room.");

            if (!completed) {
                waitingRoom.enter(patient);
                return;
            }

            Logger.log(
                this + " is now available.",
                patient + " left."
            );

            treatedCount.V();
        }
    }

    private boolean treat() {
        try {
            Thread.sleep(
                MIN_TREAT_MS
                    + random.nextInt(MAX_TREAT_MS - MIN_TREAT_MS + 1)
            );
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public String toString() {
        return "Doctor " + id;
    }
}
