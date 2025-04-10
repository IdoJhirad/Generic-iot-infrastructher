package maingatewayserver.connectionService;

import maingatewayserver.request_process_sevice.RequestProcessService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

public class ConnectionService {

    private final RequestProcessService rps;
    private volatile boolean isRun = false;

    private final Selector selector;
    private final Thread selectorThread = new Thread(this::selectorOperation);

    private final HttpServiceManger httpService = new HttpServiceManger();

    //Ctor
    public ConnectionService(RequestProcessService rps) {
        System.out.println("connection sever created");
        this.rps = rps;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.out.println("selector open failed");
            throw new RuntimeException(e);
        }
    }
    public void addHTTPConnection(int port, InetAddress ip) {
        this.httpService.connect(port, ip);
    }
    public void addHttpHandler(String uri , IotHttpHandler handler) {
        System.out.println("connection service add http handler uri: "+uri+" handler: "+handler);

        if(uri == null || handler == null) {
            System.out.println("connection service add http handler got null uri: "+uri+" handler "+handler);
            throw new RuntimeException();
        }
        httpService.addHandler(uri, handler);
    }

    public void start() {
        isRun = true;
        selectorThread.start();
        httpService.start();
    }

    public void stop() {

        try {
            isRun = false;
            selector.wakeup();

            selectorThread.join();
            Set<SelectionKey> setOfKey = selector.keys();
            for (SelectionKey key : setOfKey) {
                key.channel().close();
            }
            selector.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("server closed");
    }

    private void selectorOperation() {
        System.out.println("selector operation activate");
        while (isRun) {
            try {
                selector.select();
                Set<SelectionKey> setOfKey = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = setOfKey.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey curr = keyIterator.next();
                    ((ChannelHandler) curr.attachment()).handle();

                    keyIterator.remove();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addTCPConnection(int port, InetAddress ip) {
        if (isRun) {
            throw new RejectedExecutionException();
        }
        ServerSocketChannel tcpServer;
        System.out.println("add tcp activate");
        try {
            tcpServer = ServerSocketChannel.open();
            tcpServer.bind(new InetSocketAddress(ip, port));
            tcpServer.configureBlocking(false);
            tcpServer.register(selector, SelectionKey.OP_ACCEPT, new TcpAccept(tcpServer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void addUDPConnection(int port, InetAddress ip) {
        if (isRun) {
            throw new RejectedExecutionException();
        }
        DatagramChannel udpServer;

        System.out.println("add Udp connection activate");
        try {
            udpServer = DatagramChannel.open();
            udpServer.bind(new InetSocketAddress(ip, port));
            udpServer.configureBlocking(false);
            udpServer.register(selector, SelectionKey.OP_READ, new UdpRead(udpServer, null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class TcpAccept implements ChannelHandler {
        ServerSocketChannel channel;

        protected TcpAccept(ServerSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void handle() {
            try {
                SocketChannel socketChannel = channel.accept();
                System.out.println("TCP accept");
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ, new TcpRead(socketChannel));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private class UdpRead implements RespondableChannel, ChannelHandler {

        private final DatagramChannel datagramChannel;
        private final SocketAddress address;

        protected UdpRead(DatagramChannel channel, SocketAddress address) {
            this.datagramChannel = channel;
            this.address = address;
        }

        @Override
        public void handle() {
            System.out.println("Udp handled");
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            try {
                SocketAddress address = datagramChannel.receive(buffer);
                buffer.flip();
                rps.handleRequest(new UdpRead(datagramChannel, address), buffer);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void respond(ByteBuffer byteBuffer) {
            try {
                datagramChannel.send(byteBuffer, address);
            } catch (IOException e) {
                System.out.println("respond get exception");
                throw new RuntimeException(e);
            }
        }

    }

    private class TcpRead implements ChannelHandler, RespondableChannel {

        private final SocketChannel channel;
        private final ByteBuffer buffer = ByteBuffer.allocate(1024);

        protected TcpRead(SocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void handle() {
            try {
                int byteRead = channel.read(buffer);

                if (byteRead == -1) {
                    System.out.println("Client closed connection, closing channel.");
                    channel.close();
                    return;
                }
                buffer.flip();
                rps.handleRequest(this, buffer);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void respond(ByteBuffer byteBuffer) {
            try {
                //TODO add loop!!!
                channel.write(byteBuffer);
            } catch (IOException e) {
                System.out.println("respond throw an exception");
                throw new RuntimeException(e);
            }
        }
    }
}