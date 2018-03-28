package sysfile;

import java.io.File;
import java.util.Scanner;

public class Parent {

    public int myID;
    public String typeHost;
    public boolean listening;
    public File folder;

    public Parent(int myID,String typeHost) {
        this.myID=myID;
        this.typeHost=typeHost;
        this.listening=true;
        this.folder=null;
    }

    public static void main (String[] args) {

        if (args.length!=2) {
			System.err.println("Usage: java -jar Project2/dist/Parent.jar <ID> <M|s|c>");
			System.exit(1);
        }
        
        int myID=Integer.parseInt(args[0]);
        String typeHost = args[1];
        Parent dad = new Parent(myID,typeHost);
        CommunicationHandler cH=new CommunicationHandler(dad, myID);
        
        if (args[1].equals("M")) {

            System.out.println("This is the M-server");

            boolean success= cH.estComm(typeHost);
            if (success==true) {
                System.out.println("M-server Online");
            }

        } else if (args[1].equals("s")) {

            dad.getFolderPath(myID);

            // System.out.println("This is the file-server "+args[0]);
            boolean success= cH.estComm(typeHost);
            if (success==true) {
                System.out.println("F-server "+myID+ " is Online");
            }

            

            HeartBeat heart = new HeartBeat(myID,dad,cH,dad.folder);
            heart.start();

        } else if (args[1].equals("c")) {

            System.out.println("This is the client "+args[0]);

            boolean success= cH.estComm(typeHost);
            if (success==true) {
                System.out.println("Client "+myID+ " is Online");
            }


            Scanner choice = new Scanner(System.in);
            int choiceEntry=-1;

            while (choiceEntry!=4) {

                while (choiceEntry<1 || choiceEntry >4) {

                    System.out.println("Enter one of the following commands:");
                    System.out.println("1 - Create");
                    System.out.println("2 - Read");
                    System.out.println("3 - Append");
                    System.out.println("4 - Exit");
                    System.out.println();
                    if (choice.hasNextInt()) {

                        choiceEntry=choice.nextInt();
                        if (choiceEntry<1 || choiceEntry >4) {
                            System.out.println("Invalidad input");
                        }
                        
                    }
                }

                switch (choiceEntry) {
                    case 1:
                        System.out.println("Creating");
                        System.out.println();
                        break;

                    case 2:
                        System.out.println("Reading");
                        System.out.println();
                        break;

                    case 3:
                        System.out.println("Appending");
                        System.out.println();
                        break;

                    case 4:
                        System.out.println("Saliendo");
                        choice.close();
                        System.exit(1);
                        break;
                
                    default:
                        break;
                }
                choiceEntry=-1;
            }
            choice.close();

            

        } else {

            System.err.println("Usage: java -jar Project2/dist/Parent.jar <ID> <M|s|c>");
			System.exit(1);
        }

    }

    public void terminate() {
        this.listening=false;
    }

    public void getFolderPath(int serverId){

        switch (serverId) {
            case 11:
                this.folder=new File("./Server11/");
                break;

            case 12:
                this.folder=new File("./Server12/");
                break;

            case 13:
                this.folder=new File("./Server13/");
                break;
        
            default:
                System.err.println("Usage: java -jar Project2/dist/Parent.jar <11|12|13> <s>");
                System.exit(1);
                break;
        }

    }
} 