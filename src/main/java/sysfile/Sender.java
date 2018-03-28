package sysfile;

import java.io.PrintWriter;
import java.util.Hashtable;

public class Sender {

    public Sender () {

    }

    public void sendMessage(String msg, Hashtable<Integer,PrintWriter> socket_list,int destID) {
 
        System.out.println("Sending msg to "+destID+" msg: "+msg);
        try {
            socket_list.get(destID).println(msg);
        } catch (NullPointerException err) {
            // err.printStackTrace();
            System.exit(1);
        }


    }



}