package src;

public class Reward {

    Location b;
    Double reward;

    public Reward(Location b, Double rw)
    {
        this.b=b;
        this.reward=rw;
    }
    
    public Location getB()
    {
        return this.b;
    }

    public Double getReward()
    {
        return this.reward;
    }

    public void setB(Location b)
    {
        this.b=b;
    }

    public void setReward(Double rw)
    {
        this.reward=rw;
    }
}
