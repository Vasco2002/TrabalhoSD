package src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LocationList extends ArrayList<Location> {

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.size());
        for(Location l : this){
            l.serialize(out);
        }
    }

    public static LocationList deserialize(DataInputStream in) throws IOException {
        int size = in.readInt();

        LocationList list = new LocationList();
        for(int i = 0; i < size; i++){
            list.add(Location.deserialize(in));
        }

        return list;
    }

    public String toString(){
        if(this.size() == 0) {
            return "There are no scooters available near the location provided!";
        }
        StringBuilder sb = new StringBuilder();
        for(Location l : this){
            sb.append(l.toString()).append("\n");
        }
        return sb.toString();
    }
}