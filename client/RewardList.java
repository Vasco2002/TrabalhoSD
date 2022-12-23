package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RewardList extends ArrayList<Reward> {

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.size());
        for(Reward r : this){
            r.serialize(out);
        }
    }

    public static RewardList deserialize(DataInputStream in) throws IOException {
        int size = in.readInt();

        RewardList list = new RewardList();
        for(int i = 0; i < size; i++){
            list.add(Reward.deserialize(in));
        }

        return list;
    }

    public String toString(){

        if(this.size() == 0) {
            return "There are no rewards available near the location provided!";
        }
        StringBuilder sb = new StringBuilder();
        for(Reward r : this){
            sb.append(r.toString()).append("\n");
        }
        return sb.toString();
    }
}
