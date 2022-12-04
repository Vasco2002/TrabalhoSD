package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class Map {

    private int n; // Number of lines/columns of the map
    private Location[][] map;
    private Integer reservationCode = 0; // counter of the reservation
    private Queue<Reservation> reservation; // queue with the reservation
    private List<String> clientReservation; // each customer can only make one reservation at the same time
    private ReentrantLock reservationL = new ReentrantLock();


    public Map(Integer n) 
    {
        this.n = n;
        this.map = new Location[this.n][this.n];
        for (int i=0; i<n; i++) 
        {
            for (int j=0; i<n; j++) 
            {
                Location novoLocal = new Location(i,j);
                this.map[i][j] = novoLocal;
            }
        }
        this.reservation = new LinkedList<>();
        this.clientReservation = new ArrayList<>();
    }

    // public void movimentoUtilizador(String username, Local proximo) {
    //     this.utilizadoresLocal.put(username, proximo);
    // }

    public void makeReservation(String username, Location l)
    {
        if (!this.clientReservation.contains(username) && (l.getFreeScooters() > 0)) { // the location needs to have free scooters
            Reservation reserv = new Reservation(this.reservationCode, username, l);
            this.reservation.add(reserv);
            this.clientReservation.add(username);
            this.reservationCode++;
            l.removeScotter();
        }
    }

    public Reservation getReservation()
    {
        try {
            reservationL.lock();
            return this.reservation.peek();
        } finally {reservationL.unlock();}
    }

    //Returns a list with all the locations with free scotters, in a distance
    public List<Location> locationsFreeScooters(Integer d, Location userLocation) {
        List<Location> withFreeScooters = new ArrayList<>();

        for (int i=0; i<n; i++)
        {
            for (int j=0; i<n; j++)
            {
                if(map[i][j].distance(userLocation)<=d)
                    if(map[i][j].getFreeScooters() > 0)
                        withFreeScooters.add(map[i][j]);
            }
        }

        return withFreeScooters;
    }
}