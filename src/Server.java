package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            Connection c = new Connection(this.socket);

            while (true) 
            {
                Frame frame = c.receive();

                switch(frame.tag){
                    case 0:
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
                                c.send(0, "", "Session started successfully!".getBytes());
                            }
                            else c.send(0, "", "Error - Wrong Password.".getBytes());
                        } else
                            c.send(0, "", "Error - Account doesn't exist.".getBytes());
                        break;
                    case 1:
                        // User registration
                        email = frame.username;
                        password = new String(frame.data);
                        users.l.writeLock().lock();
                        try {
                            if(users.hasUser(email))
                                c.send(1, "", "Erro - endereço de email já pertence a uma conta.".getBytes());
                            else {
                                users.addUser(email, password);
                                c.send(frame.tag, "", "Registo efetuado com sucesso!".getBytes());
                            }
                        } finally {
                            users.l.writeLock().unlock();
                        }
                        break;
                    case 2:
                        // listar locais com trotinetes livres
                        break;
                    case 3:
                        // listar recompensas
                        break;
                    case 4:
                        // reversar
                        break;
                    case 5:
                        // estacionamento
                        break;
                    case 6:
                        // há recompensas perto
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