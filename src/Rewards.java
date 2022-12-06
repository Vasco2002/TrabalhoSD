package src;

import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Rewards implements Runnable {
    
    private Map map;
    private final int D = 2;
    private ReentrantLock rewardsL = new ReentrantLock();
    private Condition cReservation = rewardsL.newCondition();
    private Condition cParking = rewardsL.newCondition();

    public Rewards(Map map)
    {
        this.map = map;
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


    public HashMap<Location,HashMap<Location,Reward>> showAllRewards()
    {
        HashMap<Location,HashMap<Location,Reward>> result = new HashMap<>();
        int i,j;
        int n = map.getN();
        for (i=0; i<n; i++) 
            for (j=0; i<n; j++) 
                result.put(map.getMap()[i][j],map.getMap()[i][j].getRewards());

        return result;
    }

    public void run() 
    {
        while(true)
        {
            this.createAllRewards();
        }
    }
}
