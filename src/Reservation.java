package src;

public class Reservation {
    
    private Integer code;
    private Location location;

    public Reservation(Integer codigo, Location location) 
    {
        this.code = codigo;
        this.location = location.clone();
    }

    public Reservation(Reservation r) 
    {
        this.code = r.code;
        this.location = r.location.clone();
    }

    public Integer getCode()
    {
        return this.code;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public Reservation clone() { return new Reservation(this); }
}
