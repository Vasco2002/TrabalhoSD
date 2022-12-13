package src;

public class Rewards implements Runnable {
    
    private Map map;
    private final int D = 2;

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
            System.out.println(locA.getFreeScooters());
            for (i=0; i<n; i++) 
            {
                for (j=0; j<n; j++)
                {
                    Location locB = map.getMap()[i][j];
                    System.out.println("Hello" + map.locationsFreeScooters(D,locB).size());
                    if(map.locationsFreeScooters(D,locB).size()==0)
                    {
                        System.out.println("HEREEEEEEEEEEEEEEE");
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
            for (j=0; j<n; j++) {
                createReward(map.getMap()[i][j]);
                System.out.println(map.getMap()[i][j].getRewards());
            }
    }


    public void run() 
    {
        while(true)
        {
            this.map.rewardsL.lock();
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
            this.map.rewardsL.unlock();
        }
    }
}
