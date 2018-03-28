package sysfile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HeartBeat extends Thread {

    private int myID;
    private Parent dad;
    private Sender sender ;
    private CommunicationHandler cH;
    private File folder;
    private List<String> setChunks;
    private List<String> chunks;

    public HeartBeat (int myID,Parent dad, CommunicationHandler cH,File folder) {
        super("Receiver");
        this.dad=dad;
        this.myID=myID;
        this.sender= new Sender();
        this.cH=cH;
        this.folder=folder;
        this.setChunks=new ArrayList<String>();
        this.chunks=new ArrayList<String>();

    }

    @Override
    public void run() {
        String msg;

        while (dad.listening) {

            // do something
            msg=this.generateMsg(this.folder);
            sender.sendMessage(msg, cH.peers_listen,10);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                System.out.println(ex.getStackTrace());
            }
            

        }

        //System.out.println("...thump thump... from server: "+myID);

    }

    public String generateMsg(File folder){

        String datas=this.enquiry(folder);

        String msg=this.myID+","+"hb"+","+datas;

        return msg;
    }

    public String enquiry(File folder){
        String update=null;
        File[] filesFolder = folder.listFiles();

        for (File file:filesFolder) {
            if (file.isFile()) {
                this.chunks.add(file.getName());
                this.chunks.add(String.valueOf(file.lastModified()));
                this.setChunks.add(chunks.toString());
                this.chunks.clear();
            }
        }

        update=setChunks.toString();
        setChunks.clear();

        return update;

    }
}