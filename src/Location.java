package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Location {
    public int x;
    public int y;
    public int freeScooters; //number of free scooters at this location
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    public HashMap<Location,Reward> rewards;
    // lA --> map lB reward(A->B)

    public Location(int x, int y) 
    {
        this.x = x;
        this.y = y;
        this.freeScooters = 0;
        this.rewards = new HashMap<>();
    }

    public Location(int x, int y, int nScooters)
    {
        this.x = x;
        this.y = y;
        this.freeScooters = nScooters;
        this.rewards = new HashMap<>();
    }

    public Location(Location pos) 
    {
        this.x = pos.x;
        this.y = pos.y;
        this.freeScooters = pos.getFreeScooters();
        this.rewards = pos.getRewards();
    }

    public int distance(Location location)
    {
        try {
            l.readLock().lock();
            location.l.readLock().lock();
            return Math.abs(location.x - this.x) + Math.abs(location.y - this.y);
        }finally {
            location.l.readLock().unlock();
            l.readLock().unlock();
        }

    }

    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location local = (Location) o;
        return this.x == local.x && this.y == local.y;
    }

    public String toString() 
    {
        return String.format("(%d,%d)",this.x,this.y);
    }

    public Location clone() { return new Location(this); }

    public int getX() {
        try {
            l.readLock().lock();
            return x;
        }finally {l.readLock().unlock();}
    }

    public int getY() {
        try {
            l.readLock().lock();
            return y;
        }finally {l.readLock().unlock();}
    }

    public int getFreeScooters()
    {
        try {
            l.readLock().lock();
            return freeScooters;
        }
        finally {l.readLock().unlock();}
    }

    public void addScotter()
    {
        l.writeLock().lock();
        this.freeScooters++;
        l.writeLock().unlock();
    }

    public void removeScotter()
    {
        l.writeLock().lock();
        this.freeScooters--;
        l.writeLock().unlock();
    }

    public HashMap<Location,Reward> getRewards()
    {
        return this.rewards;
    }

    public void setRewards(HashMap<Location,Reward> lr)
    {
        this.rewards = lr;
    }

    public void addReward(Reward r)
    {
        if(this.rewards==null) this.rewards = new HashMap<>();
        this.rewards.put(r.b,r);
    }

    public String rewardsToString(Location f){

        try {
            l.readLock().lock();
            if(this.getRewards()==null) return "There's no rewards around your area!";
            String s = "";
            for (Location l : this.getRewards().keySet()) {
                s += "(" + l.x + "," + l.y + ") - " + f.toString() + ": " + this.getRewards().get(l).getReward() + "€ \n";
            }
            return s;
        }finally {l.readLock().unlock();}

    }


    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.x);
        out.writeInt(this.y);
        out.writeInt(this.freeScooters);
    }

    public static Location deserialize(DataInputStream in) throws IOException {
        return new Location(in.readInt(),in.readInt(),in.readInt());
    }
}
