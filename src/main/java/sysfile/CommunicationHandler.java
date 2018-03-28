package sysfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class CommunicationHandler {
    private Parent dad;
    private int myID;
    public Hashtable<Integer,String> fsList;// F-server connection hash table
    public Hashtable<Integer,String> cList;// Clients connection hash table
    public Hashtable<Integer,String> ms;// M-server connection hash table
    public static final int PORT_NUMBER_LISTEN=1895;
    public Hashtable<Integer,PrintWriter> peers_listen;// Hash table that contains writeSockets
    public int numberconneEspect;
    public Hashtable<Integer,Socket> sockets_ht;


    public CommunicationHandler(Parent dad, int myID) {
        this.dad=dad;
        this.myID=myID;
        this.fsList=new Hashtable<Integer,String>();
        this.cList=new Hashtable<Integer,String>();
        this.ms=new Hashtable<Integer,String>();
        this.peers_listen=new Hashtable<Integer,PrintWriter>();
        this.numberconneEspect=0;
        this.sockets_ht=new Hashtable<Integer,Socket>();
    }

    public boolean estComm(String typeHost) {
        boolean success=false;

        switch (typeHost) {
            case "M":
                
                this.numberconneEspect=5;
                this.getList(typeHost);
                this.connectAll(fsList);
                this.connectAll(cList);
                boolean listSuccess = this.listeNewConnection();
                if (listSuccess) {
                    success=true;
                } else {
                    success=false;
                }
                
                
                break;
        
            case "s":
                this.numberconneEspect=3;
                this.getList(typeHost);
                this.connectAll(ms);
                this.connectAll(cList);
                listSuccess = this.listeNewConnection();
                if (listSuccess) {
                    success=true;
                } else {
                    success=false;
                }
                break;

            default:
                this.numberconneEspect=4;
                this.getList(typeHost);
                this.connectAll(ms);
                this.connectAll(fsList);
                listSuccess = this.listeNewConnection();
                if (listSuccess) {
                    success=true;
                } else {
                    success=false;
                }
                break;
        }
        return success;
    }

    public void getList(String typeHost){

        switch (typeHost) {
            case "M":
                // F-server
                for (int i=11;i<=13;i++){
                    this.fsList.put(i, "dc"+Integer.toString(i)+".utdallas.edu");
                }
                // Clients list
                for (int i=1;i<=2;i++){
                    this.cList.put(i, "dc0"+Integer.toString(i)+".utdallas.edu");
                }
                break;

            case "s":
                //M-server
                this.ms.put(10,"dc10.utdallas.edu");

                // Clients list
                for (int i=1;i<=2;i++){
                    this.cList.put(i, "dc0"+Integer.toString(i)+".utdallas.edu");
                }
                break;
        
            default: //Clients
                //M-server
                this.ms.put(10,"dc10.utdallas.edu");

                // F-server
                for (int i=11;i<=13;i++){
                    this.fsList.put(i, "dc"+Integer.toString(i)+".utdallas.edu");
                }
                break;
        }

    }

    public void connectAll (Hashtable<Integer,String> peers){
        for(Integer id : peers.keySet()) {
            this.connect(id, peers.get(id));
        }
    }

    public void connect (int id, String hostname) {
         //System.out.println("Connection to hostId: "+id+", hostname: "+hostname);

        try {
            Socket s = new Socket(hostname, CommunicationHandler.PORT_NUMBER_LISTEN);
            BufferedReader readSocket = new BufferedReader (new InputStreamReader(s.getInputStream()));
            PrintWriter writeSocket = new PrintWriter(s.getOutputStream(),true);

            writeSocket.println(this.myID);// Sent my ID to my neighbor
            String reply = readSocket.readLine();// Wait for its reply
            System.out.println("Host "+Integer.toString(id)+" said "+reply);
            this.sockets_ht.put(id,s);
            this.peers_listen.put(id, writeSocket);// Add the writeSocket to the receiver's hosts list
            Receiver newClient = new Receiver(readSocket, this.dad,this,id);// Stablich receiver socket
            newClient.start();

        } catch (IOException ex) {
            System.out.println("Couldn't get I/O for the connection to " + hostname); 
            
        }
        
    }

    public boolean listeNewConnection() {
        boolean success=false;

        //System.out.println("Listening incoming connections...");

        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER_LISTEN)) {

            while (this.sockets_ht.size()<this.numberconneEspect) {
                Socket NewClient = serverSocket.accept();
                BufferedReader readSocket = new BufferedReader ( new InputStreamReader (NewClient.getInputStream()));
                PrintWriter writeSocket = new PrintWriter (NewClient.getOutputStream(),true);

                int id=Integer.parseInt(readSocket.readLine());
                writeSocket.println("Hi");
                System.out.println("Accepted Host "+id+" connection request.");
                this.sockets_ht.put(id,NewClient);
                this.peers_listen.put(id, writeSocket);// Add the writeSocket to the receiver's clients list 
                Receiver newClient = new Receiver(readSocket, this.dad,this,id);// Stablich receiver socket
                newClient.start();
            }
            success=true;
        } catch (IOException e) {
            success=false;
            e.printStackTrace();
        }
        return success;

    }

    public void reconnection() {

        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER_LISTEN)) {
            Socket NewClient = serverSocket.accept();
            BufferedReader readSocket = new BufferedReader ( new InputStreamReader (NewClient.getInputStream()));
            PrintWriter writeSocket = new PrintWriter (NewClient.getOutputStream(),true);
            int id=Integer.parseInt(readSocket.readLine());
            writeSocket.println("Hi");
            System.out.println("Accepted Host "+id+" connection request.");
            this.sockets_ht.put(id,NewClient);
            this.peers_listen.put(id, writeSocket);// Add the writeSocket to the receiver's clients list
            Receiver newClient = new Receiver(readSocket, this.dad,this,id);// Stablich receiver socket
            newClient.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}