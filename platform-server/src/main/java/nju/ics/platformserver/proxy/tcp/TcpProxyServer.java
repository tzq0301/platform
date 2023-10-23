package nju.ics.platformserver.proxy.tcp;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.proxy.ProxyServer;
import nju.ics.platformserver.proxy.TargetServer;
import nju.ics.platformserver.proxy.strategy.ProxyStrategy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class TcpProxyServer implements ProxyServer {
    private final int port;

    private final Set<TargetServer> targetServers;

    private final ProxyStrategy proxyStrategy;

    private volatile boolean stopped;

    public TcpProxyServer(int port, ProxyStrategy proxyStrategy) {
        this.port = port;
        this.proxyStrategy = proxyStrategy;
        this.targetServers = new HashSet<>();
        this.stopped = false;
    }

    @Override
    public synchronized void startAsDaemon() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(this.port)) {
                while (!stopped) {
                    Socket sourceSocket = serverSocket.accept();

                    new Thread(() -> {
                        TargetServer targetServer = this.proxyStrategy.select(this.targetServers);

                        try (Socket source = sourceSocket;
                             Socket target = new Socket(targetServer.getHost(), targetServer.getPort())) {
                            proxy(source, target);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                }
            } catch (IOException e) {
                throw new RuntimeException(String.format("port = %d", port), e);
            }
        }).start();
    }

    private void proxy(@Nonnull Socket source, @Nonnull Socket target) throws IOException {
        // source -> target
        new Thread(() -> {
            try (var targetOutputStream = target.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = source.getInputStream().read(buffer)) != -1) {
                    targetOutputStream.write(buffer, 0, bytesRead);
                    targetOutputStream.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        // source <- target
        byte[] buffer = new byte[4096];
        int bytesRead;

        try {
            while ((bytesRead = target.getInputStream().read(buffer)) != -1) {
                source.getOutputStream().write(buffer, 0, bytesRead);
                source.getOutputStream().flush();
            }
        } catch (SocketException ignored) {
            // socket closed by target
        }
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
