package src;

import java.util.HashMap;
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

    public Location(Location pos) 
    {
        this.x = pos.x;
        this.y = pos.y;
        this.freeScooters = pos.getFreeScooters();
        this.rewards = pos.getRewards();
    }

    public int distance(Location l)
    {
        return Math.abs(l.x - this.x) + Math.abs(l.y - this.y);
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

    public String rewardsToString(HashMap<Location,Reward> r){
        try {
            l.readLock().lock();
            if(r==null) return "There's no rewards around your area!";
            String s = "";
            for (Location l : r.keySet()) {
                s += "Location " + "(" + l.getX() + "," + l.getY() + "): " + r.get(l).getReward() + "\n";
            }
            return s;
        }finally {l.readLock().unlock();}

    }
}
