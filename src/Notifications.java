package src;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Notifications implements Runnable {

    private static HashMap<String,User> users = new HashMap<>();

    private static Map map;

    private ReentrantReadWriteLock l;

    public Notifications(Map m, HashMap<String,User> u, ReentrantReadWriteLock l){
        map = m;
        users = u;
        this.l = l;
    }

    public void sendNotifications() throws IOException {
        l.readLock().lock();
        for(User u: users.values())
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
                map.notifL.lock();

                while (!map.isaReward())
                    map.cNot.await();
               this.sendNotifications();

                map.aReward = false;
                map.notifL.unlock();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
