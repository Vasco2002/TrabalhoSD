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
        Demultiplexer m = new Demultiplexer(new Connection(s));

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
                m.send(0, email, password.getBytes());
                String response = new String(m.receive(0));
                if(!response.startsWith("Erro")) {
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
                m.send(1, email, password.getBytes());
                String response = new String(m.receive(1));
                if(!response.startsWith("Erro")) {
                    username = email;
                }
                System.out.println("\n" + response + "\n");
            }
        }

        while (true) {
            System.out.print("***Welcome to scooters app!***\n"
                    + "\n"
                    + "Enter your current location:\n" 
                    + "x: ");
            int x = stdin.read();
            System.out.print("\ny: ");
            int y = stdin.read();
            Location pos = new Location(x,y);
            // Falta ver se está dentro dos limites
        }

        final String finalUsername = username;
        boolean exit = false;

        while (!exit) {
            System.out.print("\n***Welcome to scooters app!***\n"
                    + "\n"
                    + "What do you want to do?\n"
                    + "1) Search close scooters.\n"
                    + "2) Book scooter.\n"
                    + "3) Park scooter.\n"
                    + "4) Serch close rewards.\n"
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
                    // m.send(2,blabla)
                    break;
                case 2:
                    // m.send(4,blabla)
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