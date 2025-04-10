package test;

import gatewayServer.GatewayServer;

import static java.lang.Thread.sleep;

public class ServerJesonTest {
    public static void main(String[] args) throws InterruptedException {
        GatewayServer gatewayServer = new GatewayServer();
        gatewayServer.start();
        sleep(50000);
       // gatewayServer.shutDown();

    }
}

class ClientTCP {
    public static void main(String[] args) {

    }
}