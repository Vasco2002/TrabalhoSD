package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;


/**
 * Client-side implementation of the program.
 *
 * Instance of <code>Server</code> must be running before running instance of <code>Client</code>.
 * Multiple instances of this class can run at the same time.
 */
public class Client {
    public static void main(String[] args) throws Exception 
    {
        Socket s = new Socket("localhost", 12345);
        Demultiplexer m = new Demultiplexer(new TaggedConnection(s));

        m.start();

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        String username = null;

        while (username == null) {
            System.out.print("**** Welcome to scooters app! ****\n"
                           + "\n"
                           + "Do you have an account?\n"
                           + "1) Yes.\n"
                           + "2) No.\n"
                           + "\n"
                           + "Enter the correspondent value: ");
            String option = stdin.readLine();
            if(option.equals("1")) {
                System.out.print("***LOG IN***\n"
                                + "\n"
                                + "Email: ");
                String email = stdin.readLine();
                System.out.print("Password: ");
                String password = stdin.readLine();
                m.send(0, email, 0, 0, 0, password.getBytes());
                String response = new String(m.receive(0));
                if(!response.startsWith("Error")) {
                    username = email;
                }
                System.out.println("\n" + response + "\n");
            }
            else if (option.equals("2")) {
                System.out.print("***CREATE ACCOUNT***\n"
                        + "\n"
                        + "Email: ");
                String email = stdin.readLine();
                System.out.print("Password: ");
                String password = stdin.readLine();
                m.send(1, email, 0, 0, 0, password.getBytes());
                String response = new String(m.receive(1));
                if(!response.startsWith("Error")) {
                    username = email;
                }
                System.out.println("\n" + response + "\n");
            }
        }


        System.out.print("***Welcome to scooters app!***");

        // Falta ver se está dentro dos limites

        boolean exit = false;

        while (!exit) {
            System.out.print("\n***Welcome to scooters app!***\n"
                    + "\n"
                    + "What do you want to do?\n"
                    + "1) Search close scooters.\n"
                    + "2) Book scooter.\n"
                    + "3) Park scooter.\n"
                    + "4) Search close rewards.\n"
                    + "5) Show all rewards.\n"
                    + "0) Exit.\n"
                    + "\n"
                    + "Enter the correspondent value: ");
            int option = stdin.read();
            switch(option) {
                case 0:
                    exit = true;
                    break;
                case 1:
                    System.out.println("Enter your current location:\n"
                            + "x: ");
                    int x = stdin.read();
                    System.out.print("\ny: ");
                    int y = stdin.read();
                    System.out.println("Radius:");
                    int r = stdin.read();
                    m.send(2, username, x, y, r, null);
                    String response = new String(m.receive(2));
                    System.out.println("\n" + response + "\n");
                    break;
                case 2:
                    System.out.println("Enter your current location:\n"
                            + "x: ");
                    x = stdin.read();
                    System.out.print("\ny: ");
                    y = stdin.read();
                    m.send(3, username, x, y, 0, null);
                    response = new String(m.receive(3));
                    System.out.println("\n" + response + "\n");
                    break;
                case 3:
                    // m.send(5,blabla)
                    break;
                case 4:
                    // m.send(6,blabla)
                    break;
                case 5:
                    // m.send(3,blabla)
                    break;
            }
        }

        m.close();
    }
}