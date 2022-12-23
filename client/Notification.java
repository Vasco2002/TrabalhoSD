package src;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class Notification implements Runnable{

    Demultiplexer m;

    public Notification(Demultiplexer m)
    {
        this.m = m;
    }

    public void run()
    {
        while(true){
            try {
                String response = new String(m.receive(9));

                System.out.println("\n NOTIFICAÇÃO \n" + response + "\n");

                sleep(1000);

                System.out.print("\n***Welcome to scooters app!***\n"
                        + "\n"
                        + "What do you want to do?\n"
                        + "1) Search close scooters.\n"
                        + "2) Book scooter.\n"
                        + "3) Park scooter.\n"
                        + "4) Search close rewards.\n"
                        + "5) Show all rewards.\n"
                        + "6) Active/Desactive notifications. \n"
                        + "0) Exit.\n"
                        + "\n"
                        + "Enter the correspondent value: ");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
