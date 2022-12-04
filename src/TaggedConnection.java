package src;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Container for a message, with extra information.
 *
 * Using a frame, we can send messages that contain information about the type of message it is and the user who sent the message.
 */


public class TaggedConnection implements AutoCloseable {
    private final DataInputStream dis;
    private final DataOutputStream dos;
    private final Lock rl = new ReentrantLock();
    private final Lock wl = new ReentrantLock();

    public TaggedConnection(Socket socket) throws IOException {
        this.dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void send(Frame frame) throws IOException {
        try {
            wl.lock();
            this.dos.writeInt(frame.tag);
            this.dos.writeInt(frame.data.length);
            this.dos.write(frame.data);
            this.dos.flush();
        }
        finally {
            wl.unlock();
        }
    }

    public void send(int tag, String username,int x, int y, int r, byte[] data) throws IOException {
        this.send(new Frame(tag, username, x, y , r, data));
    }

    public Frame receive() throws IOException {
        int tag;
        String username;
        int x, y, r;
        byte[] data;
        try {
            rl.lock();
            tag = this.dis.readInt();
            username = this.dis.readUTF();
            x = this.dis.readInt();
            y = this.dis.readInt();
            r = this.dis.readInt();
            int n = this.dis.readInt();
            data = new byte[n];
            this.dis.readFully(data);
        }
        finally {
            rl.unlock();
        }
        return new Frame(tag, username, x, y , r, data);
    }

    @Override
    public void close() throws IOException {
        this.dis.close();
        this.dos.close();
    }
}