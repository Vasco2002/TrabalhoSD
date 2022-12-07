package src;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Map {

    private int n; // Number of lines/columns of the map
    private Location[][] map;
    private Integer reservationCode = 0; // counter of the reservation
    public ReentrantLock lCounter = new ReentrantLock();

    public ReentrantLock rewardsL = new ReentrantLock();
    public Condition c = rewardsL.newCondition();

    boolean aReward = false;




    public Map(Integer n) 
    {
        this.n = n;
        this.map = new Location[this.n][this.n];
        for (int i=0; i<n; i++) 
        {
            for (int j=0; j<n; j++)
            {
                Location novoLocal = new Location(i,j);
                this.map[i][j] = novoLocal;
            }
        }
    }


    public Location[][] getMap()
    {
        return this.map;
    }

    public int getN()
    {
        return this.n;
    }

    public boolean isaReward() {
        try {
            lCounter.lock();
            return aReward;
        } finally { lCounter.unlock(); }
    }

    public void makeReservation(User user, Location l)
    {
        user.l.lock();
        // verify if the user does not have reservations and if there are free scooters in that location
        if (user.getReserv()==null && (l.getFreeScooters() > 0)) 
        {


            //decreases the number of free scooters in the location
            l.removeScotter();

            //associate revervation to user
            user.setReserv(new Reservation(this.reservationCode, l,LocalDateTime.now()));

            //increases the number of global reservations done
            lCounter.lock();
            this.reservationCode++;
            lCounter.unlock();

            // was done a reservation, the rewards have to be recalculated
            rewardsL.lock();
            this.aReward = true;
            c.signalAll();
            rewardsL.unlock();


        }
        user.l.unlock();
    }

    // If there is a reward associated, returns the price of the reward
    // Else returns the price of the deslocation
    public double parkScooter(User user, Location l) 
    {
        double price;
        user.l.lock();

        // retira da localização anterior e adiciona na presente
        // decrease number of free scooters in

        // get the location where the reservation was done
        Location prev = user.getReserv().getLocation();

        // increase number of free scooters in this location
        l.addScotter();

        // confirm if the deslocation has reward
        if(prev.getRewards().get(l)!=null)
        {
            price = prev.getRewards().get(l).getReward();
        }
        else price = -(0.7*prev.distance(l)+0.3*ChronoUnit.MINUTES.between(user.getReserv().getReservationDate(), LocalDateTime.now()))/4;

        // remove reservation of user
        user.setReserv(null);

        // was done a parking, the rewards have to be recalculated
        rewardsL.lock();
        this.aReward = true;
        c.signalAll();
        rewardsL.unlock();

        user.l.unlock();

        return price;
    }

    //Returns a list with all the locations with free scotters, in a distance
    public List<Location> locationsFreeScooters(Integer d, Location location) 
    {
        List<Location> withFreeScooters = new ArrayList<>();

        for (int i=0; i<n; i++)
        {
            for (int j=0; i<n; j++)
            {
                if(map[i][j].distance(location)<=d)
                    if(map[i][j].getFreeScooters() > 0)
                        withFreeScooters.add(map[i][j]);
            }
        }

        return withFreeScooters;
    }


    public HashMap<Location, HashMap<Location,Reward>> showAllRewards()
    {
        HashMap<Location,HashMap<Location,Reward>> result = new HashMap<>();
        int i,j;
        for (i=0; i<n; i++)
            for (j=0; i<n; j++)
                result.put(map[i][j],map[i][j].getRewards());

        return result;
    }

}