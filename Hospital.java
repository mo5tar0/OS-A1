import java.util.Scanner;

/**
 * Entry point: reads the input, creates the doctors and patients,
 * and reports when the simulation is complete.
 */
public class Hospital {
    private static final long ARRIVAL_GAP_MS = 300;

    public static void main(String[] args) throws InterruptedException {
        Scanner in = new Scanner(System.in);
        int capacity = readPositive(in, "Waiting room capacity: ");
        int doctorCount = readPositive(in, "Number of doctors: ");
        int patientCount = readPositive(in, "Number of patients: ");
        System.out.println();

        WaitingRoom waitingRoom = new WaitingRoom(capacity);
        TreatmentRooms treatmentRooms = new TreatmentRooms();
        Semaphore treatedCount = new Semaphore(0);

        for (int i = 1; i <= doctorCount; i++) {
            new Doctor(i, waitingRoom, treatmentRooms, treatedCount).start();
        }

        Patient[] patients = new Patient[patientCount];
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < patientCount; i++) {
            patients[i] = new Patient(i + 1, waitingRoom);
            if (i > 0) {
                names.append(" ");
            }
            names.append(patients[i]);
        }
        Logger.log("Patients arriving:", names.toString());

        for (Patient p : patients) {
            p.start();
            Thread.sleep(ARRIVAL_GAP_MS);
        }

        for (int i = 0; i < patientCount; i++) {
            treatedCount.P();
        }

        waitingRoom.close(doctorCount);

        Logger.log(
                "All patients have been treated.",
                "Simulation completed.");

    }

    private static int readPositive(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextInt()) {
                if (!in.hasNext()) {
                    System.err.println("\nNo input.");
                    System.exit(1);
                }
                in.next();
                System.out.println("Please enter a whole number greater than 0.");
                continue;
            }
            int n = in.nextInt();
            if (n > 0) {
                return n;
            }
            System.out.println("Please enter a whole number greater than 0.");
        }
    }
}