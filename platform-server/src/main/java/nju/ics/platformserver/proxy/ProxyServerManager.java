package nju.ics.platformserver.proxy;

import nju.ics.platformserver.net.Protocol;
import nju.ics.platformserver.proxy.strategy.ProxyStrategy;
import nju.ics.platformserver.proxy.tcp.TcpProxyServerFactory;
import nju.ics.platformserver.proxy.udp.UdpProxyServerFactory;

import java.util.HashMap;
import java.util.Map;

public class ProxyServerManager {
    private final String host;

    private final Map<Integer, ProxyServer> proxyServerMap;

    private final ProxyServerFactory tcpProxyServerFactory;

    private final ProxyServerFactory udpProxyServerFactory;

    public ProxyServerManager(String host, ProxyStrategy proxyStrategy) {
        this.host = host;
        this.tcpProxyServerFactory = new TcpProxyServerFactory(proxyStrategy);
        this.udpProxyServerFactory = new UdpProxyServerFactory(proxyStrategy);
        this.proxyServerMap = new HashMap<>();
    }

    public synchronized void register(Protocol protocol, int proxyPort, int actualPort) {
        ProxyServer proxyServer = proxyServerMap.computeIfAbsent(proxyPort, port -> {
            ProxyServerFactory proxyServerFactory = switch (protocol) {
                case TCP -> tcpProxyServerFactory;
                case UDP -> udpProxyServerFactory;
            };
            ProxyServer server = proxyServerFactory.newProxyServer(port);
            server.startAsDaemon();
            return server;
        });
        proxyServer.addTargetServer(new TargetServer(host, actualPort));
    }

    public synchronized void unregister(int proxyPort, int actualPort) {
        ProxyServer proxyServer = proxyServerMap.get(proxyPort);
        proxyServer.stop();
        proxyServer.removeTargetServer(new TargetServer(host, actualPort));
    }
}
