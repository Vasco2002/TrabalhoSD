package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Notifications implements Runnable {

    private static HashMap<String,User> users = new HashMap<>();

    private static Map map;

    public Notifications(Map m,  HashMap<String,User> u){
        this.map = m;
        this.users = u;
    }

    public void sendNotifications() throws IOException {
        for(User u: this.users.values())
        {
            Location pos = u.getPos();
            if(u.getWantNotification())
            {
                RewardList l = map.showSomeRewards(pos,2);
                u.getTaggedConnection().send(9,l);
            }
        }
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
