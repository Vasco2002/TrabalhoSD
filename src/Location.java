package src;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Location {
    public int x;
    public int y;
    public int freeScooters; //number of free scooters at this location
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Location(int x, int y) 
    {
        this.x = x;
        this.y = y;
        this.freeScooters = 0;
    }

    public Location(Location pos) 
    {
        this.x = pos.x;
        this.y = pos.y;
        this.freeScooters = pos.freeScooters;
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

}
