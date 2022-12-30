package src;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class User {
 
    private String password;
    private Reservation reserv;
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

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
        this.l.writeLock().lock();
        this.password=pass;
        this.l.writeLock().unlock();
    }

    public String getPassword()
    {
        try{
            this.l.readLock().lock();
            return this.password;
        }
        finally {
            this.l.readLock().unlock();
        }

    }

    public void setReserv(Reservation r)
    {
        this.l.writeLock().lock();
        this.reserv=r;
        this.l.writeLock().unlock();
    }

    public Reservation getReserv()
    {
        try{
            this.l.readLock().lock();
            return this.reserv;
        }
        finally {
            this.l.readLock().unlock();
        }

    }

    public void setListRewards(RewardList listRewards) {
        this.l.writeLock().lock();
        this.listRewards = listRewards;
        this.l.writeLock().unlock();
    }

    public RewardList getListRewards() {
        try{
            this.l.readLock().lock();
            return listRewards;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public void setPos(Location pos) {
        this.l.writeLock().lock();
        this.pos = pos;
        this.l.writeLock().unlock();
    }

    public Location getPos() {
        try{
            this.l.readLock().lock();
            return pos;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public Boolean getWantNotification() {
        try{
            this.l.readLock().lock();
            return wantNotification;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public void setWantNotification(Boolean wantNotification) {
        this.l.writeLock().lock();
        this.wantNotification = wantNotification;
        this.l.writeLock().unlock();
    }

    public TaggedConnection getTaggedConnection() {
        try{
            this.l.readLock().lock();
            return taggedConnection;
        }
        finally {
            this.l.readLock().unlock();
        }
    }

    public void setTaggedConnection(TaggedConnection c) {
        this.l.writeLock().lock();
        this.taggedConnection = c;
        this.l.writeLock().unlock();
    }
}
