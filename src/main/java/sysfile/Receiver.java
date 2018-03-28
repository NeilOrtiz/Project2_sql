package sysfile;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver extends Thread {
    private BufferedReader readSocket;
    private Parent dad;
    private CommunicationHandler cH;
    private int idGuest;

    public Receiver(BufferedReader readSocket,Parent dad,CommunicationHandler cH,int idGuest) {
        super("Receiver");
        this.dad=dad;
        this.cH=cH;
        this.readSocket=readSocket;
        this.idGuest=idGuest;
    }

    @Override
	public void run() {
        String msg="Connection lost";
        int counter=0;

        while (dad.listening) {

            if (dad.typeHost.equals("M")||dad.typeHost.equals("c")) {

                try {
                    msg =readSocket.readLine();

                    if (msg!=null){
                        System.out.println(msg);
                        counter=0;
                    } else {
                        counter++;
                        if (counter%4==0) {
                            System.out.println("Connection lost with F-server "+idGuest);
                            cH.peers_listen.remove(idGuest); // remove socket id from hashtable
                            cH.sockets_ht.remove(idGuest);// remove socket id from hashtable
                            readSocket.close();
                            cH.reconnection(); // wait a new connection request
                            System.out.println("Connection restored with F-server "+idGuest);
                            break; // finish thread
                            
                        } else {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                System.out.println(ex.getStackTrace());
                            }

                        }
                    }
                    
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }
                

            } else {

                try {
                    msg =readSocket.readLine();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    System.exit(1);
                }

            }

            




        }

        long threadId =Thread.currentThread().getId();
        System.out.println("Thread # "+threadId+" from F-server id "+idGuest+" has finished");

        

    }

}