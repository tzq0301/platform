package nju.ics.platformserver.proxy.udp;

import lombok.extern.slf4j.Slf4j;
import nju.ics.platformserver.proxy.ProxyServer;
import nju.ics.platformserver.proxy.TargetServer;
import nju.ics.platformserver.proxy.strategy.ProxyStrategy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
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
    public synchronized void startAsDaemon() {
        new Thread(() -> { // daemon task
            DatagramSocket sourceSocket;
            try {
                sourceSocket = new DatagramSocket(this.port);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            byte[] receivedFromSource = new byte[1024];
            DatagramPacket sourcePacket = new DatagramPacket(receivedFromSource, receivedFromSource.length);

            try {
                sourceSocket.receive(sourcePacket);  // <- block here to wait for data
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // handle new proxy
            new Thread(() -> {
                TargetServer targetServer = this.proxyStrategy.select(this.targetServers);
                try (DatagramSocket targetSocket = new DatagramSocket()) {
                    // source -> target
                    new Thread(() -> {
                        String dataFromSource = new String(sourcePacket.getData(), sourcePacket.getOffset(), sourcePacket.getLength(), StandardCharsets.UTF_8);
                        byte[] dataToTarget = dataFromSource.getBytes(StandardCharsets.UTF_8);

                        try {
                            DatagramPacket targetPacket = new DatagramPacket(dataToTarget, dataToTarget.length, InetAddress.getByName(targetServer.getHost()), targetServer.getPort());
                            targetSocket.send(targetPacket);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();

                    // source <- target
                    while (true) {
                        try {
                            byte[] receivedFromTarget = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(receivedFromTarget, receivedFromTarget.length, InetAddress.getByName(targetServer.getHost()), targetServer.getPort());
                            targetSocket.receive(packet);
                            String dataFromTarget = new String(packet.getData(), packet.getOffset(), packet.getLength());
                            byte[] dataToSource = dataFromTarget.getBytes(StandardCharsets.UTF_8);
                            sourcePacket.setData(dataToSource);
                            sourceSocket.send(sourcePacket);
                        } catch (IOException e) {
                            log.warn(e.getMessage());
                            break;
                        }
                    }
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (!sourceSocket.isClosed()) {
                        sourceSocket.close();
                    }
                }
            }).start();
        }).start();
    }

    @Override
    public synchronized void stop() {
        this.stopped = true;
    }

    @Override
    public synchronized void addTargetServer(TargetServer targetServer) {
        this.targetServers.add(targetServer);
    }

    @Override
    public synchronized void removeTargetServer(TargetServer targetServer) {
        this.targetServers.remove(targetServer);
    }
}
