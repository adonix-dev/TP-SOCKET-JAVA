package Client;

import java.io.IOException;

abstract class Client {

    private static int clientIdCounter = 0;
    private int clientId;
    protected String hostname;
    protected int destPort;

    Client(String hostname, int port) {
        this.clientId = ++clientIdCounter;
        this.hostname = hostname;
        this.destPort = port;
    }

    public int getClientId() {
        return this.clientId;
    }

    protected void displayClientMessage(String message) {
        System.out.println("Client " + this.getClientId() + " : " + message);
    }

    protected void displayClientErrorMessage(String message) {
        System.err.println("Client " + this.getClientId() + " : " + message);
    }

    public abstract String getClientIp() throws IOException;

    public abstract int getClientPort() throws IOException;

    public abstract String getServerIp() throws IOException;

    public abstract int getServerPort() throws IOException;

}
