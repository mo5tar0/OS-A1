/**
 * A patient arrives at the hospital and tries to enter the waiting room.
 * If the room is full, the patient waits outside until a seat is free
 * (handled inside WaitingRoom.enter).
 */
public class Patient extends Thread {
    private final int id;
    private final WaitingRoom waitingRoom;

    public Patient(int id, WaitingRoom waitingRoom) {
        this.id = id;
        this.waitingRoom = waitingRoom;
    }

    @Override
    public void run() {
        waitingRoom.arriveAndEnter(this);
    }

    @Override
    public String toString() {
        return "P" + id;
    }
}