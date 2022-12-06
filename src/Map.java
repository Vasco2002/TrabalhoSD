package src;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Map {

    private int n; // Number of lines/columns of the map
    private Location[][] map;
    private Integer reservationCode = 0; // counter of the reservation
    public ReentrantLock lCounter = new ReentrantLock(); 



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
    }


    public Location[][] getMap()
    {
        return this.map;
    }

    public int getN()
    {
        return this.n;
    }


    public void makeReservation(User user, Location l)
    {
        user.l.lock();
        // verifica se o user já tem alguma reserva e se há scooters livres
        while (user.getReserv()==null && (l.getFreeScooters() > 0)) { // the location needs to have free scooters
            lCounter.lock();
            user.setReserv(new Reservation(this.reservationCode, l));
            this.reservationCode++;
            lCounter.unlock();
            l.removeScotter();
        }
        user.l.unlock();
    }

    // TO DO : adicionar preço em função do tempo
    public double parkScooter(User user, Location l) 
    {
        // tens que ir à location da reserva, pegar nela e fazer a distancia de manhaten e devolver o preco
        // se houver recompensa, devolve o preço da recompensa
        
        double price;
        user.l.lock();

        // retira da localização anterior e adiciona na presente
        Location prev = user.getReserv().getLocation();
        prev.removeScotter();
        l.addScotter();

        // ver se tem reward
        if(prev.getRewards().get(l)!=null)
        {
            price = prev.getRewards().get(l).getReward();
        }
        else price = -prev.distance(l);

            
        // retirar reservation do user
        user.setReserv(null);
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


}