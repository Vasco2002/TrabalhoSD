package src;

import java.net.Socket;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

class Action implements Runnable {
    Demultiplexer b; // Demultiplexer
    int am; // Amount of operations
    int mode; // -1 for random operations
    boolean v; // Verbose
    Socket s;
    String user;

    // Constructor with Verbose option
    public Action(Demultiplexer b, int am, int mode, boolean v, Socket s, String user) {
        this.b = b;
        this.am = am;
        this.mode = mode;
        this.v = v;
        this.s = s;
        this.user = user;
    }

    // Default Constructor
    public Action(Demultiplexer b, int am, int mode, Socket s, String user) {
        this.b = b;
        this.am = am;
        this.mode = mode;
        this.v = false;
        this.s = s;
        this.user = user;
    }

    public void run() {
        int op = mode;
        Random rand = new Random();
        String response;
        ReentrantLock l = new ReentrantLock();
        try {

            // Make 'am' number of random operations in random accounts
            for (int m = 0; m < am; m++) {
                if (mode == -1)
                    op = rand.nextInt(3); // Random Operation

                // Operation Switch
                switch (op) {

                    case 0:
                        b.send(0, "", 0, 0, 0, "".getBytes());
                        b.receive(0);
                        s.shutdownInput();
                        break;
                    case 1:
                        b.send(1, user, rand.nextInt(20), rand.nextInt(20), 2, "".getBytes());
                        response = new String(b.receive(1));
                        if (v) {

                            System.out.println(
                                    "[" + Thread.currentThread().getName().toUpperCase() + "] Search close scooters");
                            System.out.println("\n" + response + "\n");

                        }
                        break;

                    case 2:
                        l.lock();
                        b.send(2, user, rand.nextInt(20), rand.nextInt(20), 2, "".getBytes());
                        response = new String(b.receive(2));
                        if (v) {
                            System.out.println(
                                    "[" + Thread.currentThread().getName().toUpperCase() + "] Book scooters");
                            System.out.println("\n" + response + "\n");
                        }
                        if (!response.equals("There are no scooters near this location!")) {
                            String[] out = response.split(" ");
                            b.send(3, user, rand.nextInt(20), rand.nextInt(20), Integer.parseInt(out[1]),
                                    "".getBytes());
                            response = new String(b.receive(3));
                            if (v) {
                                System.out.println(
                                        "[" + Thread.currentThread().getName().toUpperCase() + "] Park scooters");
                                System.out.println("\n" + response + "\n");

                            }
                        }
                        l.unlock();
                        break;

                    case 4:
                        b.send(4, user, rand.nextInt(20), rand.nextInt(20), rand.nextInt(10), "".getBytes());
                        response = new String(b.receive(4));
                        if (v) {

                            System.out.println(
                                    "[" + Thread.currentThread().getName().toUpperCase() + "] Search close rewards");
                            System.out.println("\n" + response + "\n");

                        }
                        break;

                    case 5:
                        b.send(5, user, 0, 0, 0, "".getBytes());
                        response = new String(b.receive(5));
                        if (v) {

                            System.out.println(
                                    "[" + Thread.currentThread().getName().toUpperCase() + "] Show all rewards");
                            System.out.println("\n" + response + "\n");

                        }
                        break;

                    case 6:
                        Boolean wantNotific = true;
                        if (wantNotific) {
                            // quer desativar notificação
                            wantNotific = false;
                            b.send(6, user, 0, 0, 0, "".getBytes());
                            if (v) {
                                System.out.println("Desativou notificações! D:");
                                System.out.println(
                                        "[" + Thread.currentThread().getName().toUpperCase() + "] Show all rewards");
                            }
                        } else {
                            b.send(6, user, rand.nextInt(20), rand.nextInt(20), 0, "".getBytes());
                            wantNotific = true;
                            if (v) {
                                System.out.println("Ativou notificações! ^-^");
                                System.out.println(
                                        "[" + Thread.currentThread().getName().toUpperCase() + "] Show all rewards");
                            }
                        }
                        break;

                }
            }
        } catch (Exception e) {
            // do something
        }
    }
}
