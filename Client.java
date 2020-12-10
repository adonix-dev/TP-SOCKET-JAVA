import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client {

    private static int clientIdCounter = 0;

    private int clientId;
    private String hostname;
    private int port;

    private boolean connected;
    private Socket socket;
    private BufferedReader inSocket;
    private PrintStream outSocket;

    Client(String hostname, int port){
        this.clientId = ++clientIdCounter;
        this.hostname = hostname;
        this.port = port;
        this.connected = false;
        this.displayClientMessage("started");
    }

    private void displayClientMessage(String message){
        System.out.println("Client " + this.getClientId() + " : " + message);
    }

    private void displayClientErrorMessage(String message){
        System.err.println("Client " + this.getClientId() + " : " + message);
    }

    public int getClientId(){
        return this.clientId;
    }

    public String getClientIp() throws IOException {
        if(this.socket != null) return this.socket.getLocalAddress().getHostAddress();
        else throw new IOException("Not connected to server");
    }

    public int getClientPort() throws IOException {
        if(this.socket != null) return this.socket.getLocalPort();
        else throw new IOException("Not connected to server");
    }

    public String getServerIp() throws IOException {
        if(this.socket != null) return this.socket.getInetAddress().getHostAddress();
        else throw new IOException("Not connected to server");
    }

    public int getServerPort() throws IOException {
        if(this.socket != null) return this.socket.getPort();
        else throw new IOException("Not connected to server");
    }

    private void connect() {

        try{
            this.socket = new Socket(this.hostname, this.port);
            this.inSocket = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outSocket = new PrintStream(this.socket.getOutputStream());
            this.connected = true;
            this.displayClientMessage("(" + getClientIp() + ":" + getClientPort() + ") connected to " + this.getServerIp() + ":" + getServerPort());
        }
        catch(IOException ioe){
            connected = false;
            this.displayClientErrorMessage("Erreur de connexion : " + ioe.getMessage());
        }
    }

    private String send(String message)  throws IOException{
        this.outSocket.println(message);
        this.displayClientMessage("Message \"" + message + "\" SENT to server");
        //can be delayed
        return this.inSocket.readLine();
    }

    private void work(String message){

        if(!this.connected)
            this.connect();

        try {
            String response = send(message);
            this.displayClientMessage("Server returned : \"" + response + "\"");
        }
        catch (IOException ioe){
            this.displayClientErrorMessage("Erreur de communication : " + ioe.getMessage());
        }

    }

    public void checkPair(String user, String password){

        String messageOut = "CHK " + user + " " + password;
        this.displayClientMessage("CHK " + user + " " + password);
        this.work(messageOut);
    }

    public void disconnect(){
        try{
            this.socket.close();
        }
        catch (IOException ioe){
            this.displayClientErrorMessage("Impossible de fermer la connexion : " + ioe.getMessage());
        }
    }

    public static void main(String[] args) {

        Client c1 = new Client("localhost", 12345);
        c1.checkPair("Toto","Toto");
        c1.checkPair("","");
        c1.checkPair("Toto","tata");
        c1.disconnect();
    }
}

