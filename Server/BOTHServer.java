package Server;

import as.ListeAuth;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BOTHServer implements Runnable{

    private String name;
    private int UDPPort;
    private int TCPort;

    private Thread tcpThread;
    private Thread udpThread;

    public BOTHServer(String name, int UDPPort, int TCPort){
        this.name = name;
        this.UDPPort = UDPPort;
        this.TCPort = TCPort;

        this.displayServerMessage("started");
    }

    private void displayServerMessage(String message){
        System.out.println("Server " + this.name + " : " + message);
    }

    private void displayServerErrorMessage(String message){
        System.err.println("Server " + this.name + " : " + message);
    }

    private void start() {

        TCPServer tcpAS = new TCPServer("TCP " + this.name, this.TCPort);
        this.tcpThread = new Thread(tcpAS);
        this.tcpThread.start();

        UDPServer udpAS = new UDPServer("UDP " + this.name, null, this.UDPPort);
        this.udpThread = new Thread(udpAS);
        this.udpThread.start();
    }

    private void stop() {
        this.tcpThread.interrupt();
        this.udpThread.interrupt();
    }

    public static void main(String[] args) {

        BOTHServer AS = new BOTHServer("Authentication Service", 12345, 12346);
        AS.start();
        AS.stop(); //never accessed
    }

    @Override
    public void run() {
        BOTHServer AS = new BOTHServer("Authentication Service", 12345, 12346);
        AS.start();
    }
}
