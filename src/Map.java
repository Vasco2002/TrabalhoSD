package src;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Map {

    private int n; // Number of lines/columns of the map
    private Location[][] map;

    private final int D = 2;
    private Integer reservationCode = 0; // counter of the reservation
    public ReentrantLock lCounter = new ReentrantLock();

    public ReentrantLock rewardsL = new ReentrantLock();
    public ReentrantLock notifL = new ReentrantLock();
    public Condition c = rewardsL.newCondition();

    public Condition cNot = notifL.newCondition();
    boolean aReward = false;

    public Map(Integer n) {
        this.n = n;
        this.map = new Location[this.n][this.n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Location novoLocal = new Location(i, j);
                this.map[i][j] = novoLocal;
            }
        }
        this.fillScooters();
    }

    public void fillScooters() {
        Random rand = new Random();
        int random = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i < 5 && j < 5)
                    random = rand.nextInt(i + j + 1);
                else
                    random = ((i + j) / (rand.nextInt(j + i) + 1)) / 2;
                random--;
                for (; random > 0; random--)
                    this.map[i][j].addScotter();
            }
        }
    }

    public int getD() {
        return D;
    }

    public Location[][] getMap() {
        return this.map;
    }

    public int getN() {
        return this.n;
    }

    public boolean isaReward() {
        try {
            lCounter.lock();
            return aReward;
        } finally {
            lCounter.unlock();
        }
    }

    /**
     * Make a reservation
     * 
     * @param user
     * @param location
     * @return location if the reservation is done successfully
     * @return null if there are no scooters free in the near location
     */
    public Location makeReservation(User user, Location location) {
        try {
            user.l.writeLock().lock();
            // verify if the user does not have reservations and if there are free scooters
            // in that location
            Location aux = closestScooter(location);
            if (aux != null) {
                // decreases the number of free scooters in the location
                aux.removeScotter();

                lCounter.lock();
                // associate revervation to user
                user.setReserv(new Reservation(this.reservationCode, aux, LocalDateTime.now()));

                // increases the number of global reservations done

                this.reservationCode++;
                lCounter.unlock();

                // was done a reservation, the rewards have to be recalculated
                rewardsL.lock();
                notifL.lock();
                this.aReward = true;
                c.signalAll();
                cNot.signalAll();
                notifL.unlock();
                rewardsL.unlock();

                return aux;
            } else
                return null;

        } finally {
            user.l.writeLock().unlock();
        }
    }

    // If there is a reward associated, returns the price of the reward
    // Else returns the price of the deslocation
    public double parkScooter(User user, Location location) {
        double price = 0;
        user.l.writeLock().lock();

        // retira da localização anterior e adiciona na presente
        // decrease number of free scooters in

        // get the location where the reservation was done
        Location prev = user.getReserv().getLocation();

        // increase number of free scooters in this location
        location.addScotter();

        // confirm if the deslocation has reward
        if (prev.getRewards() != null && prev.getRewards().containsKey(location)) {
            price = prev.getRewards().get(location).getReward();
        }

        else
            price = -(0.7 * prev.distance(location)
                    + 0.3 * ChronoUnit.MINUTES.between(user.getReserv().getReservationDate(), LocalDateTime.now())) / 4;

        // remove reservation of user
        user.setReserv(null);

        // was done a parking, the rewards have to be recalculated
        rewardsL.lock();
        notifL.lock();
        this.aReward = true;
        c.signalAll();
        cNot.signalAll();
        notifL.unlock();
        rewardsL.unlock();

        user.l.writeLock().unlock();
        return price;
    }

    // Returns a list with all the locations with free scotters, in a distance
    public LocationList locationsFreeScooters(Integer d, Location location) {
        LocationList withFreeScooters = new LocationList();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (map[i][j].distance(location) <= d)
                    if (map[i][j].getFreeScooters() > 0)
                        withFreeScooters.add(map[i][j]);
            }
        }

        return withFreeScooters;
    }

    public Location closestScooter(Location location) {
        LocationList locations = locationsFreeScooters(this.D, location);
        double m = Double.POSITIVE_INFINITY;
        Location r = null;
        double aux;
        for (Location l : locations) {
            aux = l.distance(location);
            if (m > aux) {
                r = l;
                m = aux;
            }
        }
        return r;
    }

    public String showAllRewards() {
        try {
            this.rewardsL.lock();
            String result = "";
            int i, j;
            for (i = 0; i < n; i++)
                for (j = 0; j < n; j++) {
                    if (map[i][j].getRewards() != null)
                        result += map[i][j].rewardsToString(map[i][j]) + "\n";
                }

            return result;
        } finally {
            rewardsL.unlock();
        }
    }

    public RewardList showAllRewards2() {
        try {
            rewardsL.lock();
            RewardList result = new RewardList();
            int i, j;
            for (i = 0; i < n; i++)
                for (j = 0; j < n; j++) {
                    if (this.map[i][j].getRewards() != null) {
                        for (Reward r : this.map[i][j].getRewards().values())
                            result.add(r);
                    }
                }

            return result;
        } finally {
            rewardsL.unlock();
        }
    }

    public RewardList showSomeRewards(Location l, int D) {
        try {
            this.rewardsL.lock();
            RewardList rlist = new RewardList();

            int i, j;

            int initX = l.getX() - D;
            if (initX < 0)
                initX = 0;

            int endX = l.getX() + D;
            if (endX >= this.n)
                endX = this.n - 1;

            int initY = l.getY() - D;
            if (initY < 0)
                initY = 0;

            int endY = l.getY() + D;
            if (endY >= this.n)
                endY = this.n - 1;

            for (i = initX; i <= endX; i++)
                for (j = initY; j <= endY; j++)
                    if (map[i][j].getRewards() != null && map[i][j].distance(l) <= 2)
                        for (Reward r : map[i][j].getRewards().values()) {
                            rlist.add(r);
                        }

            return rlist;
        } finally {
            rewardsL.unlock();
        }
    }

    public String printMap() {
        String r = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                r += Integer.toString(this.map[i][j].getFreeScooters()) + " ";
            }
            r += "\n";
        }
        return r;
    }

    public String printMapRewards() {
        String r = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (this.map[i][j].getRewards() != null)
                    r += "X";
                else
                    r += "-";
            }
            r += "\n";
        }
        return r;
    }

}