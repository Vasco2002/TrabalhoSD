package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Notifications implements Runnable {

    private List clientes;
    private Socket socket;

    public Notifications() throws IOException {
        ServerSocket serverSocket = new ServerSocket(55555);

        Socket socket = serverSocket.accept();
    }

    public void run() {

    }



}
