package Server;

import as.ListeAuth;
import as.ListeAuthAdapt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServer extends ListeAuthAdapt implements Runnable{

    private String name;
    private int port;

    private ServerSocket socketListener;
    private Socket socketService;
    private BufferedReader inSocket;
    private PrintStream outSocket;

    public TCPServer(String name, int port){
        this.name = name;
        this.port = port;

        this.displayServerMessage("started");
    }

    private void displayServerMessage(String message){
        System.out.println("Server " + this.name + " : " + message);
    }

    private void displayServerErrorMessage(String message){
        System.err.println("Server " + this.name + " : " + message);
    }

    private void initListener() throws IOException{

            this.socketListener = new ServerSocket(this.port);
            this.displayServerMessage("Listning on port : " + this.port);
    }

    private void initIOStream() throws IOException {
        this.inSocket = new BufferedReader (new InputStreamReader(this.socketService.getInputStream()));
        this.outSocket = new PrintStream(this.socketService.getOutputStream());
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void start() {

        try {

            initListener();

            try {

                while(true) {

                        this.socketService = this.socketListener.accept();
                        this.initIOStream();
                        String messageIn = "begin";

                        while(messageIn != null){ //communication client

                            try {

                                messageIn = this.inSocket.readLine();

                                if(messageIn != null){
                                    this.displayServerMessage("Received from client : " + messageIn);

                                    String messageOut = this.worker(messageIn);

                                    this.outSocket.println(messageOut);
                                }
                            }
                            catch (IOException ioe) {
                                this.displayServerErrorMessage("Erreur de connexion : " + ioe.getMessage());
                            }
                        }

                        try {
                            this.socketService.close();
                        }
                        catch (IOException ioe){
                            this.displayServerErrorMessage("Erreur de connexion avec le client : " + ioe.getMessage());
                        }
                    }
                }
            catch (IOException ioe){
                this.displayServerErrorMessage("Erreur d'accept : " + ioe.getMessage());
            }
            this.socketListener.close();
        }
        catch(IOException ioe) {
            this.displayServerErrorMessage("Erreur de création du server socket :" + ioe.getMessage());
        }
    }

    private String worker(String messageIn){

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
                    //TP1 : only check
                    return "ERROR bad_request";
                case "MOD":
                    //TP1 : only check
                    return "ERROR bad_request";
                case "ADD":
                    //TP1 : only check
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

    private void stop(){
        try {
            this.socketListener.close();
        }
        catch (IOException ioe){
            this.displayServerErrorMessage("Can't close socket : " + ioe.getMessage());
        }
    }

    public static void main(String[] args) {

        TCPServer AS = new TCPServer("TCP Authentication Service", 12345);
        AS.start();
        AS.stop(); //never accessed
    }

    @Override
    public void run() {
        this.start();
        this.stop(); //never accessed
    }
}
