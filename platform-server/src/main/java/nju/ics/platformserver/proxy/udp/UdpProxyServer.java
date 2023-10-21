package nju.ics.platformserver.proxy.udp;

import nju.ics.platformserver.proxy.ProxyServer;
import nju.ics.platformserver.proxy.TargetServer;
import nju.ics.platformserver.proxy.strategy.ProxyStrategy;

import java.util.HashSet;
import java.util.Set;

public class UdpProxyServer implements ProxyServer {
    private final int port;

    private final Set<TargetServer> targetServers;

    private final ProxyStrategy proxyStrategy;

    private volatile boolean stopped;

    public UdpProxyServer(int port, ProxyStrategy proxyStrategy) {
        this.port = port;
        this.proxyStrategy = proxyStrategy;
        this.targetServers = new HashSet<>();
        this.stopped = false;
    }

    @Override
    public void startAsDaemon() {
        throw new UnsupportedOperationException();

//        new Thread(() -> {
//            try (DatagramSocket datagramSocket = new DatagramSocket(this.port)) {
//                while (!stopped) {
//
//                }
//            } catch (SocketException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
    }

//    @Override
//    public synchronized void startAsDaemon() {
//        new Thread(() -> {
//            try (DatagramSocket ds = new DatagramSocket(this.port)) {
//                while (this.loop.get()) {
//                    byte[] buffer = new byte[1024];
//                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//                    ds.receive(packet);
//
//                    String data = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
//
//                    forward(Objects.requireNonNull(targetServers.getLast()), data);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//    }

    @Override
    public void stop() {
        this.stopped = true;
    }

//    public synchronized void forward(TargetServer targetServer, String data) {
//        try (DatagramSocket ds = new DatagramSocket()) {
//            ds.connect(InetAddress.getByName("localhost"), targetServer.getPort());
//            byte[] bytes = data.getBytes();
//            DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
//            ds.send(packet);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public synchronized void addTargetServer(TargetServer targetServer) {
        this.targetServers.add(targetServer);
    }

    @Override
    public synchronized void removeTargetServer(TargetServer targetServer) {
        this.targetServers.remove(targetServer);
    }
}
