package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Notifications implements Runnable {

    private static HashMap<String,User> users = new HashMap<>();

    private static Map map;

    private ReentrantReadWriteLock l;

    public Notifications(Map m, HashMap<String,User> u, ReentrantReadWriteLock l){
        this.map = m;
        this.users = u;
        this.l = l;
    }

    public void sendNotifications() throws IOException {
        l.readLock().lock();
        for(User u: this.users.values())
        {
            Location pos = u.getPos();
            if(u.getWantNotification())
            {
                RewardList l = map.showSomeRewards(pos,2);
                u.getTaggedConnection().send(9,l);
            }
        }
        l.readLock().unlock();
    }


    public void run()
    {
        while(true)
        {
            try {
                this.map.notifL.lock();

                while (!map.isaReward())
                    this.map.cNot.await();
               this.sendNotifications();

                map.aReward = false;
                this.map.notifL.unlock();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
