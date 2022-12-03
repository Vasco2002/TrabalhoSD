package src;

public class Reservation {
    
    private Integer code;
    private String username;
    private Location location;

    public Reservation(Integer codigo, String username, Location location) 
    {
        this.code = codigo;
        this.username = username;
        this.location = location.clone();
    }

    public Reservation(Reservation r) 
    {
        this.code = r.code;
        this.username = r.username;
        this.location = r.location.clone();
    }

    public Integer getCode()
    {
        return this.code;
    }

    public Reservation clone() { return new Reservation(this); }
}
