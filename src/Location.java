package src;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Location {
    public int x;
    public int y;
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Location(int x, int y) 
    {
        this.x = x;
        this.y = y;
    }

    public Location(Location pos) 
    {
        this.x = pos.x;
        this.y = pos.y;
    }

    public int distancia(Location l) 
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
}
