package src;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Users {
    private final HashMap<String, String> users;  //key:username(email)/value:password
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Users() {
        this.users = new HashMap<>();
    }

    public boolean hasUser(String email) {
        return users.containsKey(email);
    }

    public String getPassword(String email) {
        return users.get(email);
    }

    public void addUser(String email, String password) {
        users.put(email, password);
    }

    /* Nem vou por os imports porque ainda não sei se vamos usar assim :)

    public void serialize(String filepath) throws IOException {
        FileOutputStream fos = new FileOutputStream(filepath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
        fos.close();
    }

    public static Accounts deserialize(String filepath) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filepath);
        ObjectInputStream ois = new ObjectInputStream(fis);
        users users = (users) ois.readObject();
        ois.close();
        fis.close();
        return accounts;
    }
     */
}
