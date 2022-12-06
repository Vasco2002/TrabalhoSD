package src;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Rewards implements Runnable {
    
    private Map map;
    private final int D = 2;

    private int rcode;

    public Rewards(Map map)
    {
        this.map = map;
        this.rcode = 0;
    }
    
    public void createReward(Location locA)
    {
        locA.setRewards(null); //clean list of rewards
        int i,j;
        int n = this.map.getN();

        if(locA.getFreeScooters()>1)
        {
            for (i=0; i<n; i++) 
            {
                for (j=0; i<n; j++) 
                {
                    Location locB = map.getMap()[i][j];

                    if(map.locationsFreeScooters(D,locB).size()==0)
                    {
                        Double priceReward = (locA.getFreeScooters() * 0.6 + locA.distance(locB) * 0.4)/2;
                        locA.addReward(new Reward(locB, priceReward));
                    }
                }
            }
        }
    }


    public void createAllRewards()
    {
        int i,j;
        int n = map.getN();
        for (i=0; i<n; i++) 
            for (j=0; i<n; j++) 
                createReward(map.getMap()[i][j]);
    }


    public void run() 
    {
        while(true)
        {
            this.createAllRewards(); // cria todos os rewards

            while (!map.isaReward())
            {
                try {
                    this.map.c.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            map.aReward = false;
        }
    }
}
