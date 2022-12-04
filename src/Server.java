package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

class ServerWorker implements Runnable 
{
    private Socket socket;
    private Users users;
    private Map map;

    public ServerWorker (Socket socket, String fileMap, String fileUsers) {

        // Get the map
        File f = new File(fileMap);
        if(!f.exists())
            map = new Map(20);
        // else map = Map.parse(fileMap);
        //else Location = Location.deserialize("Location.ser");
        
        
        // Get the users
        f = new File(fileUsers);
        if(!f.exists())
            users = new Users();
        // else users = Users.parse(fileUsers);
        //else users = users.deserialize("users.ser");


        this.socket = socket;
    }

    @Override
    public void run() 
    {
        String email;
        String password;
        try 
        {
            TaggedConnection c = new TaggedConnection(this.socket);

            while (true) 
            {
                Frame frame = c.receive();

                switch(frame.tag){
                    case -1:
                        //Log in
                        email = frame.username;
                        password = new String(frame.data);
                        // Search password of user saved in mem
                        String stored_password;
                        users.l.readLock().lock();
                        try {
                            stored_password = users.getPassword(email);
                        } finally {
                            users.l.readLock().unlock();
                        }
                        if (stored_password != null) 
                        {
                            if (stored_password.equals(password)) 
                            {
                                // passwords match
                                c.send(-1, "", 0,0,0,"Session started successfully!".getBytes());
                            }
                            else c.send(-1, "",0,0,0, "Error - Wrong Password.".getBytes());
                        } else
                            c.send(-1, "",0,0,0, "Error - Account doesn't exist.".getBytes());
                        break;
                    case -2:
                        // User registration
                        email = frame.username;
                        password = new String(frame.data);
                        users.l.writeLock().lock();
                        try {
                            if(users.hasUser(email))
                                c.send(-2, "",0,0,0, "Error - Username already exists".getBytes());
                            else {
                                users.addUser(email, password);
                                c.send(frame.tag, "",0,0,0, "Username added successfully!".getBytes());
                            }
                        } finally {
                            users.l.writeLock().unlock();
                        }
                        break;
                    case 1:
                        // List locations with close free scooters
                        Location pos = new Location(frame.x, frame.y);
                        List<Location> result = map.locationsFreeScooters(frame.r, pos);
                        c.send(1,"",0,0,0,result.toString().getBytes());
                        break;
                    case 2:
                        // Reservation
                        pos = new Location(frame.x, frame.y);
                        map.makeReservation(frame.username, pos);
                        c.send(2,"",0,0,0,"Reservation done successfully!".getBytes());
                        break;
                    case 3:
                        // Parking
                        pos = new Location(frame.x, frame.y);
                        map.parkScooter(frame.username, pos);
                        c.send(3,"",0,0,0,"Parking done successfully!".getBytes());
                        break;
                    case 4:
                        // There are close rewards
                        pos = new Location(frame.x, frame.y);
                        c.send(4,"",0,0,0,pos.getRewards().toString().getBytes());
                        break;
                        
                    case 5:
                        // List rewards
                        c.send(4,"",0,0,0,map.showAllRewards().toString().getBytes());
                        break;
                        
                
                }   
                
            }
        }
        catch(IOException e){}
    }
}




/**
 * Server-side implementation of the program.
 *
 * Instance of <code>Server</code> must be running before running instance(s) of <code>Client</code>.
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
            Thread worker = new Thread(new ServerWorker(socket, args[1], args[2]));
            worker.start();
        }
    }

}