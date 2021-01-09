package Client;

import java.io.IOException;

public class UDPMgtClient extends UDPClient{

    public UDPMgtClient(String hostname, int port) {
        super(hostname, port);
    }

    private void createPair(String user, String password){
        String messageOut = "ADD " + user + " " + password;
        super.displayClientMessage("ADD " + user + " " + password);
        super.work(messageOut);
    }

    private void modifyPair(String user, String password){
        String messageOut = "MOD " + user + " " + password;
        super.displayClientMessage("MOD " + user + " " + password);
        super.work(messageOut);
    }

    private void deletePair(String user, String password){
        String messageOut = "DEL " + user + " " + password;
        super.displayClientMessage("DEL " + user + " " + password);
        super.work(messageOut);
    }

    public static void main(String[] args) {

        UDPMgtClient c1 = new UDPMgtClient("localhost", 12345);
        //UDPClient c1 = new UDPClient("255.255.255.255", 12345); //broadcast
        c1.checkPair("Toto","Toto");
        c1.createPair("Antony", "MDP");
        c1.checkPair("Antony", "MDP");
        c1.modifyPair("Antony", "PDM");
        c1.checkPair("Antony", "PDM");
        c1.deletePair("Antony", "PDM");
        c1.checkPair("Antony", "PDM");
    }
}
