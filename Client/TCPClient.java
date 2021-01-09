package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class TCPClient extends Client {

    private boolean connected;
    private Socket socket;
    private BufferedReader inSocket;
    private PrintStream outSocket;

    TCPClient(String hostname, int port){
        super(hostname, port);
        this.connected = false;
        super.displayClientMessage("started");
    }

    public String getClientIp() throws IOException {
        if(!connected) return this.socket.getLocalAddress().getHostAddress();
        else throw new IOException("Not connected to server");
    }

    public int getClientPort() throws IOException {
        if(!connected) return this.socket.getLocalPort();
        else throw new IOException("Not connected to server");
    }

    public String getServerIp() throws IOException {
        if(!connected) return this.socket.getInetAddress().getHostAddress();
        else throw new IOException("Not connected to server");
    }

    public int getServerPort() throws IOException {
        if(!connected) return this.socket.getPort();
        else throw new IOException("Not connected to server");
    }

    public void connect() {

        try{
            this.socket = new Socket(this.hostname, this.destPort);
            this.inSocket = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.outSocket = new PrintStream(this.socket.getOutputStream());
            this.connected = true;
            this.displayClientMessage("(" + getClientIp() + ":" + getClientPort() + ") connected to " + this.getServerIp() + ":" + getServerPort());
        }
        catch(Exception e){ // to catch Socket exceptions (ex: bad port)
            this.connected = false;
            this.displayClientErrorMessage("Erreur de connexion : " + e.getMessage());
        }
    }

    private String send(String message) throws IOException{
        this.outSocket.println(message);
        this.displayClientMessage("Message \"" + message + "\" SENT to server as TCP packet");
        //can be delayed
        return this.inSocket.readLine();
    }

    private void work(String message){

        if(!this.connected)
            this.connect();

        try {
            if(this.connected){
                String response = send(message);
                this.displayClientMessage("Server returned : \"" + response + "\"");
            }
            else throw new IOException("Not connected");
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
            if(this.connected) this.socket.close();
            else throw new IOException("Not connected");
        }
        catch (IOException ioe){
            this.displayClientErrorMessage("Impossible de fermer la connexion : " + ioe.getMessage());
        }
    }

    public static void main(String[] args) {

        TCPClient c1 = new TCPClient("localhost", 12345);
        c1.connect();
        c1.checkPair("Toto","Toto");
        c1.checkPair("","");
        c1.checkPair("Toto","tata");
        c1.disconnect();
    }
}

