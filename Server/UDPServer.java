package Server;

import as.ListeAuth;
import as.ListeAuthAdapt;

import java.io.IOException;
import java.net.*;

public class UDPServer extends ListeAuthAdapt implements Runnable{

    private String name;
    private int port;

    private byte[] buffer;
    private DatagramSocket socketListener;
    private DatagramPacket clientDataPacket;

    private InetAddress diffusionIP;

    private InetAddress clientIP;
    private int clientPort;
    protected String clientData;


    public UDPServer(String name, String diffusion, int port){
        this.name = name;
        this.port = port;

        if(diffusion != null){
            try {
                this.diffusionIP = InetAddress.getByName("255.255.255.255");
                this.diffusionIP = InetAddress.getByName(diffusion);
            }
            catch (IOException ioe){
                this.displayServerErrorMessage("/!\\ WARNING : Can't set diffusion IP (default : 255.255.255.255) : " + ioe.getMessage());
            }
        }

        this.buffer = new byte[256];
        this.clientDataPacket = new DatagramPacket(buffer, buffer.length);

        this.displayServerMessage("started");
    }

    protected void initListener(){

        try{
            //this.socketListener.receive(this.packetReceived);
            this.socketListener = new DatagramSocket(this.port);
            this.displayServerMessage("Listning on port : " + this.port);
        }
        catch (Exception e){ // to catch DatagramSocket exceptions (ex: bad port)
            this.displayServerErrorMessage("Error creating socket : " + e.getMessage());
            this.socketListener.close();
        }
    }

    protected void listen(){
        try{
            //this.displayServerMessage("waiting...");
            this.socketListener.receive(this.clientDataPacket);

            if(this.diffusionIP == null) this.clientIP = this.clientDataPacket.getAddress();
            else this.clientIP = this.diffusionIP;

            this.clientPort = this.clientDataPacket.getPort();

            this.clientData = new String(this.buffer, 0, this.clientDataPacket.getLength());

            //this.socketListener.close();
        }
        catch (IOException ioe){ // to catch DatagramSocket exceptions (ex: bad port)
            this.displayServerErrorMessage("Error socket : " + ioe.getMessage());
            this.socketListener.close();
        }
    }

    protected void displayServerMessage(String message){
        System.out.println("Server " + this.name + " : " + message);
    }

    protected void displayServerErrorMessage(String message){
        System.err.println("Server " + this.name + " : " + message);
    }

    protected String worker(String messageIn){

        try{
            String[] params = messageIn.split(" ");

            if(params[0].equals("") || params[1].equals("") || params[2].equals("")){
                throw new Exception("Mauvaise entree");
            }

            switch (params[0]){
                case "CHK":
                    this.displayServerMessage("CHECKING " + params[1] + " " + params[2]);
                    if (ListeAuthAdapt.db.tester(params[1], params[2])) {
                        this.displayServerMessage("GOOD " + params[1] + " " + params[2]);
                        return "GOOD";
                    } else {
                        this.displayServerErrorMessage("BAD " + params[1] + " " + params[2]);
                        return "BAD";
                    }
                case "DEL":
                    //TP2 : only check
                    return "ERROR bad_request";
                case "MOD":
                    //TP2 : only check
                    return "ERROR bad_request";
                case "ADD":
                    //TP2 : only check
                    return "ERROR bad_request";
                default:
                    this.displayServerErrorMessage("Requete recu invalide.");
                    this.displayServerErrorMessage("usage : CHK|DEL|MOD|ADD user password");
                    return "ERROR bad_request";
            }

        }
        catch (Exception e){
            this.displayServerErrorMessage("Requete recu invalide.");
            this.displayServerErrorMessage("usage : CHK|DEL|MOD|ADD user password");
            //this.displayServerErrorMessage(e.getMessage());
            return "ERROR bad_request";
        }
    }

    protected void answer(String answer) {
        try {
            byte[] binaryAnswer = answer.getBytes();
            DatagramPacket serverDataPacket = new DatagramPacket(binaryAnswer, binaryAnswer.length, this.clientIP, this.clientPort);
            this.socketListener.send(serverDataPacket);
        }
        catch (Exception e){
            this.displayServerErrorMessage("Anwering error : " + e.getMessage());
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    protected void start() {
        this.initListener();

        while(true) {
            this.listen();
            String answer = this.worker(this.clientData);
            this.answer(answer);
        }
    }

    protected void stop(){
        this.socketListener.close();
    }

    public static void main(String[] args) {

        UDPServer AS = new UDPServer("UDP Authentication Service", null, 12345);
        //UDPServer AS = new UDPServer("UDP Authentication Service", "255.255.255.255", 12345); //broadcast
        AS.start();
        AS.stop(); //never accessed
    }


    @Override
    public void run() {
        this.start();
        this.stop(); //never accessed
    }
}
