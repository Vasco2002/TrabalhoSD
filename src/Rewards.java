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
                    System.out.println(i + "," + j + ": " + map.locationsFreeScooters(D,locB).size());
                    if(map.locationsFreeScooters(D,locB).size()==0)
                    {
                        System.out.println("HEREEEEEEEEEEEEEEE");
                        Double priceReward = (locA.getFreeScooters() * 0.6 + locA.distance(locB) * 0.4)/2;
                        locA.addReward(new Reward(locB, priceReward));
                        System.out.println("N rewards:" + locA.getRewards().size());
                    }
                }
            }
        }
    }


    public void createReward2()
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
                if(map.locationsFreeScooters(D,locB).size()==0)
                {
                    //System.out.println("LocB:" + i + "," + j + map.locationsFreeScooters(D,locB).size());
                    this.createAllRewards2(locB);
                }
            }
        }



    }

    public void createAllRewards2(Location locB)
    {
        int i,j;
        int n = map.getN();

        for (i=0; i<n; i++)
            for (j=0; j<n; j++)
            {
                Location locA = map.getMap()[i][j];
                // apenas cria reward se a localização tiver scooters livres
                if(locA!=locB && locA.getFreeScooters()>1)
                {
                    //System.out.println("criou:" + i + "," + j);
                    Double priceReward = (locA.getFreeScooters() * 0.6 + locA.distance(locB) * 0.4)/2;
                    locA.addReward(new Reward(locB, priceReward));
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
            //this.createAllRewards(); // cria todos os rewards
            this.createReward2();

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
