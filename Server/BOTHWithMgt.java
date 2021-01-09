package Server;

        import as.ListeAuth;

        import java.io.BufferedReader;
        import java.io.PrintStream;
        import java.net.ServerSocket;
        import java.net.Socket;

public class BOTHWithMgt {

    private String name;
    private int UDPPort;
    private int TCPort;
    private int UPDMgtPort;

    private Thread BOTHThread;
    private Thread MgtThread;

    public BOTHWithMgt(String name, int UDPPort, int TCPort, int UPDMgtPort){
        this.name = name;
        this.UDPPort = UDPPort;
        this.TCPort = TCPort;
        this.UPDMgtPort = UPDMgtPort;

        this.displayServerMessage("started");
    }

    private void displayServerMessage(String message){
        System.out.println("Server " + this.name + " : " + message);
    }

    private void displayServerErrorMessage(String message){
        System.err.println("Server " + this.name + " : " + message);
    }

    private void start() {

        BOTHServer BOTHAS = new BOTHServer("BOTH " + this.name, this.UDPPort, this.TCPort);
        this.BOTHThread = new Thread(BOTHAS);
        this.BOTHThread.start();

        UDPMgtServer MgtAS = new UDPMgtServer(this.UPDMgtPort);
        this.MgtThread = new Thread(MgtAS);
        this.MgtThread.start();
    }

    private void stop() {
        this.BOTHThread.interrupt();
        this.MgtThread.interrupt();
    }

    public static void main(String[] args) {

        BOTHWithMgt AS = new BOTHWithMgt("Authentication Service & Management", 12345, 12346,28414  );
        AS.start();
        AS.stop(); //never accessed
    }
}
