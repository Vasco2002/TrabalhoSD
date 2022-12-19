package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Reward {

    Location a;
    Location b;
    Double reward;

    public Reward(Location a, Location b, Double rw)
    {
        this.a=a;
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
    public void setA(Location a)
    {
        this.a=a;
    }
    public void setB(Location b)
    {
        this.b=b;
    }

    public void setReward(Double rw)
    {
        this.reward=rw;
    }

    public void serialize(DataOutputStream out) throws IOException{
        out.writeInt(this.a.getX());
        out.writeInt(this.a.getY());
        out.writeInt(this.b.getX());
        out.writeInt(this.b.getY());
        out.writeDouble(this.reward);
    }

    public static Reward deserialize(DataInputStream in) throws IOException{
        Location a = new Location(in.readInt(),in.readInt());
        Location b = new Location(in.readInt(),in.readInt());
        double reward = in.readDouble();

        return new Reward(a,b,reward);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(this.b.x).append(",").append(this.b.y).append(")")
                .append("-(").append(this.a.x).append(",").append(this.a.y).append(")")
                .append(": ").append(this.reward).append(" €");

        return sb.toString();
    }
}
