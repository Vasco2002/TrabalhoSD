package src;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Teste {

    static Random rand = new Random();

    public static void main(String[] args) throws InterruptedException, IOException {
        // Variable Declarations
        // final int TN = 2 * Runtime.getRuntime().availableProcessors(); // RECOMENDED:
        // 2*#CPUs | Threads
        // final int OP = 1000000; // RECOMENDED: 1000000 | Operations per Thread
        final int TN = 5;
        final int OP = 2;
        final boolean V = true; // RECOMENDED: false | Verbose
        final long startTime = System.nanoTime(); // Global Clock: runtime start to finish

        // ------------------------------------------------------------

        Socket s;
        try {
            s = new Socket("localhost", 5555);
            Demultiplexer m = new Demultiplexer(new TaggedConnection(s));
            m.start();

            // ------------------------------------------------------------

            try {
                for (int j = 0; j < TN; j++) {
                    m.send(7, String.valueOf(j), 0, 0, 0, "1".getBytes());
                    String response = new String(m.receive(7));
                    if (!response.startsWith("Error")) {
                    }
                    System.out.println(response);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            // ------------------------------------------------------------

            System.out.println("\nTestar Search close scooters ");
            make_operations(m, TN, OP, 1, V, s);

            System.out.println("\nTestar Book and Park scooter ");
            make_operations(m, TN, OP, 2, V, s);

            System.out.println("\nTestar Search close rewards ");
            make_operations(m, TN, OP, 4, V, s);

            // System.out.println("\nTestar show all rewards ");
            // make_operations(m, TN, OP, 5, V, s);

            System.out.println("\nTestar ativar/desativar notificações ");
            make_operations(m, TN, OP, 6, V, s);

            // Show total execution time
            System.out.println("\n[CLOCK] Total time: " + (System.nanoTime() - startTime) / 1000000 + " ms");

            // matar threads
            make_operations(m, TN, OP, 0, V, s);

        } catch (Exception e) {
            // algo
        }

    }

    // Multi threaded operations on random accounts
    public static void make_operations(Demultiplexer b, int TN, int OP, int mode, boolean V, Socket s)
            throws InterruptedException {
        Thread[] tm = new Thread[TN];
        for (int j = 0; j < TN; j++) {
            tm[j] = new Thread(new Action(b, OP, mode, V, s, String.valueOf(j)));
        }
        for (int j = 0; j < TN; j++) {
            tm[j].start();
        }
        for (int j = 0; j < TN; j++) {
            tm[j].join();
        }
    }

    // Multi threaded operations on random accounts
    public static void make_operationsRandom(Demultiplexer b, int TN, int OP, boolean V, Socket s)
            throws InterruptedException {
        Thread[] tm = new Thread[TN];

        for (int j = 0; j < TN; j++) {
            int i = rand.nextInt(7);
            tm[j] = new Thread(new Action(b, OP, i, V, s, String.valueOf(j)));
        }
        for (int j = 0; j < TN; j++) {
            tm[j].start();
        }
        for (int j = 0; j < TN; j++) {
            tm[j].join();
        }
    }

}
