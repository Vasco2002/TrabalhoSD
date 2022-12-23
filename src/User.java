package src;

import java.util.concurrent.locks.ReentrantLock;

public class User {
 
    private String password;
    private Reservation reserv;
    public ReentrantLock l = new ReentrantLock();

    RewardList listRewards = new RewardList();

    Boolean wantNotification = false;

    TaggedConnection taggedConnection;

    Location pos;

    public User(String pass)
    {
        this.password=pass;
        this.reserv=null;
    }

    public void setPassword(String pass)
    {
        this.password=pass;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setReserv(Reservation r)
    {
        this.reserv=r;
    }

    public Reservation getReserv()
    {
        return this.reserv;
    }

    public void setListRewards(RewardList listRewards) {
        this.listRewards = listRewards;
    }

    public RewardList getListRewards() {
        return listRewards;
    }

    public void setPos(Location pos) {
        this.l.lock();
        this.pos = pos;
        this.l.unlock();
    }

    public Location getPos() {
        try{
            this.l.lock();
            return pos;
        }
        finally {
            this.l.unlock();
        }
    }

    public Boolean getWantNotification() {
        try{
            this.l.lock();
            return wantNotification;
        }
        finally {
            this.l.unlock();
        }
    }

    public void setWantNotification(Boolean wantNotification) {
        this.l.lock();
        this.wantNotification = wantNotification;
        this.l.unlock();
    }

    public TaggedConnection getTaggedConnection() {
        try{
            this.l.lock();
            return taggedConnection;
        }
        finally {
            this.l.unlock();
        }
    }

    public void setTaggedConnection(TaggedConnection c) {
        this.l.lock();
        this.taggedConnection = c;
        this.l.unlock();
    }
}
