package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Map {

    private int n; // Number of lines/columns of the map
    private Location[][] map;
    private HashMap<String, Location> usersLocation;
    private HashMap<Location, Integer> freeScooters; // value is the number of dree scooters in the given location
    private Integer reservationCode = 0; // counter of the reservation
    private Queue<Reservation> reservation; // queue with the reservation
    private HashMap<String, Boolean> clientReservation; // each customer can only make one reservation at the same time


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
        this.usersLocation = new HashMap<>();
        this.freeScooters = new HashMap<>();
        this.reservation = new LinkedList<>();
        this.clientReservation = new HashMap<>();
    }

    // public void movimentoUtilizador(String username, Local proximo) {
    //     this.utilizadoresLocal.put(username, proximo);
    // }

    public void rmakeReservation(String username, Location l) 
    {
        if (this.clientReservation.containsKey(username)) 
        {
            if (!this.clientReservation.get(username) && (this.freeScooters.get(l)) > 0) { // the location needs to heve free scooters
                Reservation reserv = new Reservation(this.reservationCode, username, l);
                this.reservation.add(reserv);
                this.clientReservation.put(username, true);
                this.reservationCode++;
                int n_scooters = this.freeScooters.get(l);
                this.freeScooters.put(l, n_scooters-1);
            }
        }
        else if (this.freeScooters.get(l) > 0) {
            Reservation reserv = new Reservation(this.reservationCode, username, l);
            this.reservation.add(reserv);
            this.clientReservation.put(username, true);
            this.reservationCode++;
            int n_scooters = this.freeScooters.get(l);
            this.freeScooters.put(l, n_scooters-1);
        }
    }

    public Reservation getReservation() {
        return this.reservation.peek();
    }

    public boolean hasScooters(Location l) 
    {
        if (!this.freeScooters.containsKey(l)) return false;
        else if (this.freeScooters.get(l) == 0) return false;
        else return true;
    }

    // Devolve uma lista com os locais com trotinetes livres 
    public List<Location> locationsFreeScooters(Integer d, String username) {
        List<Location> withFreeScooters = new ArrayList<>();
        Location userLocation = this.usersLocation.get(username);

        for (Location l : this.freeScooters.keySet()) {
            if (userLocation.distance(l) > d) {
                if (this.freeScooters.get(l) > 0) withFreeScooters.add(l);
            }
        }

        return withFreeScooters;
    }


}