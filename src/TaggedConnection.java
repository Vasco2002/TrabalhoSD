package src;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Container for a message, with extra information.
 *
 * Using a frame, we can send messages that contain information about the type
 * of message it is and the user who sent the message.
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
            int tag = frame.tag;
            this.dos.writeInt(tag);
            if (tag == 4 || tag == 5 || tag == 9) {
                frame.rewardList.serialize(dos);
            } else if (tag == 3) {
                this.dos.writeDouble(frame.reserv);
            } else if (tag == 1) {
                frame.locationList.serialize(dos);
            } else {
                this.dos.writeUTF(frame.username);
                this.dos.writeInt(frame.x);
                this.dos.writeInt(frame.y);
                this.dos.writeInt(frame.r);
                this.dos.writeInt(frame.data.length);
                this.dos.write(frame.data);
            }

            this.dos.flush();
        } finally {
            wl.unlock();
        }
    }

    public void send(int tag, String username, int x, int y, int r, byte[] data) throws IOException {
        this.send(new Frame(tag, username, x, y, r, data));
    }

    public void send(int tag, RewardList rewardList) throws IOException {
        this.send(new Frame(tag, rewardList));
    }

    public void send(int tag, LocationList locationList) throws IOException {
        this.send(new Frame(tag, locationList));
    }

    public void send(int tag, double reserv) throws IOException {
        this.send(new Frame(tag, reserv));
    }

    public Frame receive() {
        int tag;
        String username;
        int x, y, r;
        byte[] data;
        try {
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
            } finally {
                rl.unlock();
            }
            return new Frame(tag, username, x, y, r, data);
        } catch (IOException e) {
            // do something my friend
            return null;
        }
    }

    public void sendClient(Frame frame) throws IOException {
        try {
            wl.lock();
            this.dos.writeInt(frame.tag);
            this.dos.writeUTF(frame.username);
            this.dos.writeInt(frame.x);
            this.dos.writeInt(frame.y);
            this.dos.writeInt(frame.r);
            this.dos.writeInt(frame.data.length);
            this.dos.write(frame.data);
            this.dos.flush();
        } finally {
            wl.unlock();
        }
    }

    public void sendClient(int tag, String username, int x, int y, int r, byte[] data) throws IOException {
        this.sendClient(new Frame(tag, username, x, y, r, data));
    }

    public Frame receiveClient() throws IOException {

        int tag;
        String username;
        int x, y, r;
        byte[] data;
        try {
            rl.lock();
            tag = this.dis.readInt();
            if (tag == 4 || tag == 5 || tag == 9) {
                RewardList rewardList = RewardList.deserialize(dis);
                return new Frame(tag, rewardList);
            } else if (tag == 3) {
                double reserv = this.dis.readDouble();
                return new Frame(tag, reserv);
            } else if (tag == 1) {
                LocationList locationList = LocationList.deserialize(dis);
                return new Frame(tag, locationList);
            } else {
                username = this.dis.readUTF();
                x = this.dis.readInt();
                y = this.dis.readInt();
                r = this.dis.readInt();
                int n = this.dis.readInt();
                data = new byte[n];
                this.dis.readFully(data);
                return new Frame(tag, username, x, y, r, data);
            }

        } finally {
            rl.unlock();
        }

    }

    @Override
    public void close() throws IOException {
        this.dis.close();
        this.dos.close();
    }
}