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
    private final String targetServersHost;

    private final Map<Integer, ProxyServer> proxyServerMap;

    private final ProxyServerFactory tcpProxyServerFactory;

    private final ProxyServerFactory udpProxyServerFactory;

    public ProxyServerManager(@Nonnull String targetServersHost, @Nonnull ProxyStrategy proxyStrategy) {
        this.targetServersHost = targetServersHost;
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
            log.info("启动 {} ProxyServer [port = {}]", protocol, proxyPort);
            return server;
        });
        proxyServer.addTargetServer(new TargetServer(targetServersHost, actualPort));
        log.info("为 ProxyServer [port = {}] 增加 TargetServer [port = {}]", proxyPort, actualPort);
    }

    public synchronized void unregister(int proxyPort, int actualPort) {
        ProxyServer proxyServer = proxyServerMap.get(proxyPort);
        proxyServer.stop();
        proxyServer.removeTargetServer(new TargetServer(targetServersHost, actualPort));
        log.info("为 ProxyServer [port = {}] 移除 TargetServer [port = {}]", proxyPort, actualPort);
    }
}
