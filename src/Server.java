package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.file.Files;
import java.nio.file.Paths;

class ServerWorker implements Runnable 
{
    private Socket socket;
    private HashMap<String,User> users = new HashMap<>();
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    private Map map;

    public ServerWorker (Socket socket, Integer NMap, String fileUsers) {

        // create map
        map = new Map(NMap);
        
        // Get the users
        File f = new File(fileUsers);
        if(f.exists())
            this.parserUser(fileUsers);

        this.socket = socket;

        Thread rewards = new Thread(new Rewards(map));
        rewards.start();

    }

    public List<String> readFile(String nomeFich) {
        List<String> lines;
        try { lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);}
        catch(IOException exc) {
            lines = new ArrayList<>();
        }
        return lines;
    }

    public void parserUser (String fileUsers) {
        List<String> linhas = readFile(fileUsers);
        String[] parts;
        for (String linha : linhas) {
            parts = linha.split(";");
            this.users.put(parts[0],new User(parts[1]));
        }
    }

    @Override
    public void run() 
    {
        String email;
        String password;
        Location pos;
        try (TaggedConnection c = new TaggedConnection(this.socket)) 
        {
            System.out.println(map.printMap());
            while (true)
            {
                Frame frame = c.receive();
                switch(frame.tag){

                    case 6:
                        System.out.println("Server: start");
                        //Log in
                        email = frame.username;
                        password = new String(frame.data);
                        // Search password of user saved in mem
                        String stored_password;
                        l.readLock().lock();
                        try {
                            stored_password = users.get(email).getPassword();
                        } finally {
                            l.readLock().unlock();
                        }
                        if (stored_password != null) 
                        {
                            if (stored_password.equals(password)) 
                            {
                                // passwords match
                                c.send(6, "", 0,0,0,"Session started successfully!".getBytes());
                            }
                            else c.send(6, "",0,0,0, "Error - Wrong Password.".getBytes());
                        } else
                            c.send(6, "",0,0,0, "Error - Account doesn't exist.".getBytes());
                        System.out.println("Server: end");
                        break;
                    case 7:
                        // User registration
                        email = frame.username;
                        password = new String(frame.data);
                        l.writeLock().lock();
                        try {
                            if(users.containsKey(email))
                                c.send(7, "",0,0,0, "Error - Username already exists".getBytes());
                            else {
                                users.put(email, new User(password));
                                c.send(frame.tag, "",0,0,0, "Username added successfully!".getBytes());
                            }
                        } finally {
                            l.writeLock().unlock();
                        }
                        break;
                    case 1:
                        // List locations with close free scooters
                        pos = map.getMap()[frame.x][frame.y];
                        List<Location> result = map.locationsFreeScooters(frame.r, pos);
                        c.send(1,"",0,0,0,result.toString().getBytes());
                        break;
                    case 2:
                        // Reservation
                        pos = map.getMap()[frame.x][frame.y];
                        map.makeReservation(users.get(frame.username), pos);
                        c.send(2,"",0,0,0,"Reservation done successfully!".getBytes());
                        break;
                    case 3:
                        // Parking
                        double price;
                        pos = map.getMap()[frame.x][frame.y];
                        if (users.get(frame.username).getReserv()!=null) {
                            price = map.parkScooter(users.get(frame.username), pos);
                            String s = "Parking done successfully!\n" + "Trip cost: " + price;
                            c.send(3, "", 0, 0, 0, s.getBytes());
                        } else c.send(3, "", 0, 0, 0, "You don't have reservation!".getBytes());
                        break;
                    case 4:
                        // There are close rewards
                        pos = map.getMap()[frame.x][frame.y];
                        c.send(4,"",0,0,0,pos.rewardsToString(pos.getRewards()).getBytes());
                        break;
                        
                    case 5:
                        // List rewards
                        c.send(4,"",0,0,0,map.showAllRewards().toString().getBytes());
                        break;
                            
                    
                }   
                    
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}




/**
 * Server-side implementation of the program.
 */
public class Server {

    // args[0] = server tcp port
    // args[1] = mapa
    // args[2] = users
    
    public static void main(String[] args) throws Exception 
    {
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        /* Every time a new Client tries to connect, accept that connection, run a worker to handle the client and go back to waiting for new clients. */
        while(true) 
        {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, Integer.parseInt(args[1]), args[2]));
            worker.start();
        }
    }

}