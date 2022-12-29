package src;

import java.time.LocalDateTime;

public class Reservation {
    
    private int code;
    private Location location;
    private LocalDateTime reservationDate;

    public Reservation(int codigo, Location location, LocalDateTime date)
    {
        this.code = codigo;
        this.location = location;
        this.reservationDate = date;
    }

    public int getCode()
    {
        return this.code;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public LocalDateTime getReservationDate()
    {
        return this.reservationDate;
    }
}
