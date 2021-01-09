package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPClient extends Client{

    private DatagramSocket socket;
    private InetAddress destAddress;

    public UDPClient(String hostname, int port) {

        super(hostname, port);

        try {
            this.destAddress = InetAddress.getByName(this.hostname);

            try{
                this.socket = new DatagramSocket();
                this.socket.setSoTimeout(5000);
                this.displayClientMessage("started");
            }
            catch (IOException ioe){
                this.displayClientErrorMessage("Can't create socket : " + ioe.getMessage());
            }
        }
        catch (IOException ioe){
            this.displayClientErrorMessage("IP Address error : " + ioe.getMessage());
        }
    }


    public String getClientIp() throws IOException {
        if(this.socket != null) return this.socket.getLocalAddress().getHostAddress();
        else throw new IOException("UDP Socket not created");
    }

    public int getClientPort() throws IOException {
        if(this.socket != null) return this.socket.getLocalPort();
        else throw new IOException("UDP Socket not created");
    }

    public String getServerIp() {
        return this.destAddress.getHostAddress();
    }

    public int getServerPort() {
        return this.destPort;
    }

    protected void work(String message) {
        try {
            if(this.destAddress != null) {

                DatagramPacket sentPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, this.destAddress, this.destPort);
                this.socket.send(sentPacket);
                this.displayClientMessage("(" + getClientIp() + ":" + getClientPort() + ") sending as UDP packet to " + this.getServerIp() + ":" + this.getServerPort());

                byte[] buffer = new byte[256];
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                try{
                    //can be delayed
                    this.socket.receive(receivedPacket);
                    String answer = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                    this.displayClientMessage("Server returned : \"" + answer + "\"");
                }
                catch (SocketTimeoutException ste){
                    this.displayClientErrorMessage("Server not responding : " + ste.getMessage());
                }
            }
            else {
                throw new IOException("Unknown host");
            }
        }
        catch (Exception e){  // to catch DatagramPacket exceptions (ex: bad port)
            this.displayClientErrorMessage("Sending error : " +e.getMessage());
        }
    }

    protected void checkPair(String user, String password){
        String messageOut = "CHK " + user + " " + password;
        this.displayClientMessage("CHK " + user + " " + password);
        this.work(messageOut);
    }

    public static void main(String[] args) {

        UDPClient c1 = new UDPClient("localhost", 12345);
        //UDPClient c1 = new UDPClient("255.255.255.255", 12345); //broadcast
        c1.checkPair("Antony","PDM");
    }
}
