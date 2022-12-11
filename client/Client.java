package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import  src.*;

/**
 * Client-side implementation of the program.
 *
 * Instance of <code>Server</code> must be running before running instance of <code>Client</code>.
 * Multiple instances of this class can run at the same time.
 */
public class Client {
    public static void main(String[] args) throws Exception 
    {
        Socket s = new Socket("localhost",5555);
        Demultiplexer m = new Demultiplexer(new TaggedConnection(s));

        m.start();

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        String username = null;

        while (username == null) {
            System.out.println("Client: start");
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
                System.out.println(email);
                m.send(6, email, 0, 0, 0, password.getBytes());
                String response = new String(m.receive(6));
                if(!response.startsWith("Error")) {
                    username = email;
                }
                System.out.println("\n" + response + "\n");
                System.out.println("Client: end");
            }
            else if (option.equals("2")) {
                System.out.print("***CREATE ACCOUNT***\n"
                        + "\n"
                        + "Email: ");
                String email = stdin.readLine();
                System.out.print("Password: ");
                String password = stdin.readLine();
                m.send(7, email, 0, 0, 0, password.getBytes());
                String response = new String(m.receive(7));
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
            String option = stdin.readLine();
            System.out.println("\nOption: " + option + "\n");
            int x, y, r;
            String response;
            switch(option) {
                case "0":
                    exit = true;
                    break;
                case "1":
                    System.out.println("Enter your current location:\n"
                            + "x: ");
                    x = Integer.parseInt(stdin.readLine());
                    System.out.println("y: ");
                    y = Integer.parseInt(stdin.readLine());
                    System.out.println("Radius:");
                    r = Integer.parseInt(stdin.readLine());
                    m.send(1, username, x, y, r, "".getBytes());
                    response = new String(m.receive(1));
                    System.out.println("\n" + response + "\n");
                    break;
                case "2":
                    System.out.println("Enter your current location:\n"
                            + "x: ");
                    x = Integer.parseInt(stdin.readLine());
                    System.out.println("y: ");
                    y = Integer.parseInt(stdin.readLine());
                    m.send(2, username, x, y, 0, "".getBytes());
                    response = new String(m.receive(2));
                    System.out.println("\n" + response + "\n");
                    break;
                case "3":
                    System.out.println("Enter the location which you want to park the scooter:\n"
                            + "x: ");
                    x = Integer.parseInt(stdin.readLine());
                    System.out.println("y: ");
                    y = Integer.parseInt(stdin.readLine());
                    m.send(3, username, x, y, 0, "".getBytes());
                    response = new String(m.receive(3));
                    System.out.println("\n" + response + "\n");
                    break;
                case "4":
                    System.out.println("Enter your current location:\n"
                            + "x: ");
                    x = stdin.read();
                    System.out.print("\ny: ");
                    y = stdin.read();
                    m.send(4, username, x, y, 0, null);
                    response = new String(m.receive(4));
                    System.out.println("\n" + response + "\n");
                    break;
                case "5":
                    m.send(5, username, 0, 0, 0, null);
                    response = new String(m.receive(5));
                    System.out.println("\n" + response + "\n");
                    break;
            }
        }

        m.close();
    }
}