package src;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Rewards implements Runnable {
    
    private Map map;

    public Rewards(Map map)
    {
        this.map = map;
    }

    public void createReward()
    {
        int i,j;
        int n = this.map.getN();

        // limpa todos os rewards antigos
        for (i=0; i<n; i++) {
            for (j = 0; j < n; j++) {
                this.map.getMap()[i][j].setRewards(null);
            }
        }

        // verifica para cada localização se tem scooters perto, se não tiver, para todas as outras
        // localizações é criada uma recompensa
        for (i=0; i<n; i++)
        {
            for (j=0; j<n; j++)
            {
                Location locB = map.getMap()[i][j];
                if(map.locationsFreeScooters(map.getD(),locB).size()==0)
                    this.createAllRewards(locB,"");
            }
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public RewardList createAllRewards(Location locB, String user)
    {
        int i,j;
        int n = map.getN();
        RewardList l = null;

        for (i=0; i<n; i++)
            for (j=0; j<n; j++)
            {
                Location locA = map.getMap()[i][j];
                // apenas cria reward se a localização tiver scooters livres
                if(locA!=locB && locA.getFreeScooters()>1) {
                    Double priceReward = round(((locA.getFreeScooters() * 0.6 + locA.distance(locB) * 0.4) / 2), 2);
                    locA.addReward(new Reward(locA, locB, priceReward));
                }
            }
        return l;
    }

    public void run() 
    {
        while(true)
        {
            this.map.rewardsL.lock();
            this.createReward();  // cria todos os rewards

            while (!map.isaReward())
            {
                try {
                    this.map.c.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            map.aReward = false;
            this.map.rewardsL.unlock();
        }
    }
}
