package src;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer {

    private TaggedConnection tc;
    private ReentrantLock l = new ReentrantLock();
    private HashMap<Integer, FrameValue> map = new HashMap<>();
    private IOException exception = null;

    private class FrameValue {
        int waiters = 0;
        Queue<byte[]> queue = new ArrayDeque<>();
        Condition c = l.newCondition();

        public FrameValue() {

        }
    }

    public Demultiplexer(TaggedConnection conn) throws IOException {
        this.tc = conn;
    }

    public void start() {
        new Thread(() -> {
            try {
                while (true) {
                    Frame frame = tc.receive();
                    l.lock();
                    try {
                        int tag = frame.tag;
                        FrameValue fv = map.get(tag);
                        if (fv == null) {
                            fv = new FrameValue();
                            map.put(frame.tag, fv);
                        }
                        if(tag == 4 || tag == 5 || tag == 9){
                            fv.queue.add(frame.rewardList.toString().getBytes());
                        }
                        else if(tag == 2 || tag == 3){
                            fv.queue.add(String.valueOf(frame.reserv).getBytes());
                        }
                        else if(tag == 1){
                            fv.queue.add(frame.locationList.toString().getBytes());
                        }
                        else
                            fv.queue.add(frame.data);
                        fv.c.signal();
                        // if one thread gets exception, wake up all
                        if (exception != null) 
                            while(fv.waiters>0) // n threads sleeping
                                fv.c.signal();
                        
                    }
                    finally {
                        l.unlock();
                    }
                }
            }
            catch (IOException e) {
                exception = e;
            }
        }).start();
    }

    public void send(Frame frame) throws IOException {
        tc.send(frame);
    }

    public void send(int tag, String username, int x, int y, int r, byte[] data) throws IOException {
        tc.send(tag, username, x, y, r, data);
    }

    public byte[] receive(int tag) throws IOException, InterruptedException {
        l.lock();
        FrameValue fv;
        try {
            fv = map.get(tag);
            if (fv == null) {
                fv = new FrameValue();
                map.put(tag, fv);
            }
            fv.waiters++;
            while(true) {
                if(! fv.queue.isEmpty()) {
                    fv.waiters--;
                    byte[] reply = fv.queue.poll();
                    if (fv.waiters == 0 && fv.queue.isEmpty())
                        map.remove(tag);
                    return reply;
                }
                if (exception != null) {
                    throw exception;
                }
                fv.c.await();
            }
        }
        finally {
            l.unlock();
        }
    }


    public void close() throws IOException {
        tc.close();
    }
}