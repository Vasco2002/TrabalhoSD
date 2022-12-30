package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.file.Files;
import java.nio.file.Paths;

class ServerWorker implements Runnable 
{
    Map map;
    HashMap<String,User> users;
    private Socket socket;

    ReentrantReadWriteLock l;

    public ServerWorker (Socket socket, Map a, HashMap<String,User> u, ReentrantReadWriteLock l) {
        this.socket = socket;
        this.map = a;
        this.users = u;
        this.l = l;
    }

    @Override
    public void run() 
    {
        String email = "";
        String password;
        Location pos;

        System.out.println(map.printMap());
        try (TaggedConnection c = new TaggedConnection(this.socket)) 
        {
            boolean exit = false;
            while (!exit)
            {
                Frame frame = c.receive();
                switch(frame.tag){

                    case 6:
                        //Log in
                        email = frame.username;
                        password = new String(frame.data);
                        // Search password of user saved in mem
                        String stored_password = null;
                        l.readLock().lock();
                        try {
                            if(users.containsKey(email)) {
                                stored_password = users.get(email).getPassword();
                                users.get(email).setTaggedConnection(c);

                                if (stored_password.equals(password))
                                {
                                    // passwords match
                                    c.send(6, "", 0,0,0,"Session started successfully!".getBytes());
                                } else c.send(6, "",0,0,0, "Error - Wrong Password.".getBytes());
                            }else c.send(6, "",0,0,0, "Error - Account doesn't exist.".getBytes());

                        } finally {
                            l.readLock().unlock();
                        }
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
                        users.get(email).setTaggedConnection(c);
                        break;
                    case 1:
                        // List locations with close free scooters
                        pos = map.getMap()[frame.x][frame.y];
                        LocationList result = map.locationsFreeScooters(frame.r, pos);
                        c.send(1, result);
                        break;
                    case 2:
                        // Reservation
                        pos = map.getMap()[frame.x][frame.y];
                        this.users.get(email).setPos(pos);
                        Location aux = map.makeReservation(users.get(frame.username),pos);
                        if (aux == null) c.send(2, "", -1,-1,-1,"".getBytes());
                        else c.send(2, "", aux.getX(),aux.getY(),this.users.get(email).getReserv().getCode(),"".getBytes());
                        System.out.println(map.printMap());
                        break;
                    case 3:
                        // Parking
                        int reserv = frame.r;
                        pos = map.getMap()[frame.x][frame.y];
                        this.users.get(email).setPos(pos);
                        c.send(3, map.parkScooter(users.get(frame.username), pos));
                        System.out.println(map.printMap());
                        break;
                    case 4:
                        // There are close rewards
                        pos = map.getMap()[frame.x][frame.y];
                        c.send(4, this.map.showSomeRewards(pos,frame.r));
                        break;
                        
                    case 5:
                        // List rewards
                        c.send(5,map.showAllRewards2());
                        System.out.println(this.map.printMapRewards());
                        break;

                    case 8:
                        // active notifications
                        // recebe posição onde o cliente pretende receber se existem notificações perto
                        pos = map.getMap()[frame.x][frame.y];
                        this.users.get(email).setPos(pos);
                        // tem de haver uma thread que fique a percorrer as localizações que precisam
                        if(this.users.get(email).getWantNotification()){
                            // quer desativar notificação
                            this.users.get(email).setWantNotification(false);
                        }
                        else{
                            // quer ativar -> adiciona o user na lista de notificações da posição
                            this.users.get(email).setWantNotification(true);
                        }
                        break;
                    case 0:
                        c.send(0, "", 0, 0, 0, "".getBytes());
                        this.users.get(email).setWantNotification(false);
                        exit = true;
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

    private static HashMap<String,User> users = new HashMap<>();

    private static Map map;
    public static ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    
    public static void main(String[] args) throws Exception 
    {
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        /* Every time a new Client tries to connect, accept that connection, run a worker to handle the client and go back to waiting for new clients. */

        // create map
        Map map = new Map(Integer.parseInt(args[1]));

        Thread rewards = new Thread(new Rewards(map));
        rewards.start();

        Thread notification = new Thread(new Notifications(map,users,l));
        notification.start();

        while(true)
        {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, map, users, l));
            worker.start();
        }
    }


}