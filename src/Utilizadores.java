package src;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Utilizadores {
    private final HashMap<String, String> utilizadores;  //key:username(email)/value:password
    public ReentrantReadWriteLock l = new ReentrantReadWriteLock();

    public Utilizadores() {
        this.utilizadores = new HashMap<>();
    }

    public boolean utilizadorExiste(String email) {
        return utilizadores.containsKey(email);
    }

    public String getPassword(String email) {
        return utilizadores.get(email);
    }

    public void addUtilizador(String email, String password) {
        utilizadores.put(email, password);
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
        Utilizadores utilizadores = (Utilizadores) ois.readObject();
        ois.close();
        fis.close();
        return accounts;
    }
     */
}
