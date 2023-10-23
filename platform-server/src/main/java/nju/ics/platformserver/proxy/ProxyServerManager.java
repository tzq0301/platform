package nju.ics.platformserver.proxy;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import nju.ics.platformserver.net.Protocol;
import nju.ics.platformserver.proxy.strategy.ProxyStrategy;
import nju.ics.platformserver.proxy.tcp.TcpProxyServerFactory;
import nju.ics.platformserver.proxy.udp.UdpProxyServerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProxyServerManager {
    private final String host;

    private final Map<Integer, ProxyServer> proxyServerMap;

    private final ProxyServerFactory tcpProxyServerFactory;

    private final ProxyServerFactory udpProxyServerFactory;

    public ProxyServerManager(@Nonnull String host, @Nonnull ProxyStrategy proxyStrategy) {
        this.host = host;
        this.tcpProxyServerFactory = new TcpProxyServerFactory(proxyStrategy);
        this.udpProxyServerFactory = new UdpProxyServerFactory(proxyStrategy);
        this.proxyServerMap = new HashMap<>();
    }

    public synchronized void register(@Nonnull Protocol protocol, int proxyPort, int actualPort) {
        ProxyServer proxyServer = proxyServerMap.computeIfAbsent(proxyPort, port -> {
            ProxyServerFactory proxyServerFactory = switch (protocol) {
                case TCP -> tcpProxyServerFactory;
                case UDP -> udpProxyServerFactory;
            };
            ProxyServer server = proxyServerFactory.newProxyServer(port);
            server.startAsDaemon();
            log.info("start {} proxy server of port {}", protocol, proxyPort);
            return server;
        });
        proxyServer.addTargetServer(new TargetServer(host, actualPort));
        log.info("add target server, proxy port = {}, actual port = {}", proxyPort, actualPort);
    }

    public synchronized void unregister(int proxyPort, int actualPort) {
        ProxyServer proxyServer = proxyServerMap.get(proxyPort);
        proxyServer.stop();
        proxyServer.removeTargetServer(new TargetServer(host, actualPort));
    }
}
