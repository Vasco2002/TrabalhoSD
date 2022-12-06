package src;

import java.util.concurrent.locks.ReentrantLock;

public class User {
 
    private String password;
    private Reservation reserv;
    public ReentrantLock l = new ReentrantLock();

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

}
