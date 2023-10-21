package nju.ics.platformserver.proxy.udp;

import nju.ics.platformserver.proxy.ProxyServer;
import nju.ics.platformserver.proxy.ProxyServerFactory;
import nju.ics.platformserver.proxy.strategy.ProxyStrategy;

public class UdpProxyServerFactory implements ProxyServerFactory {
    private final ProxyStrategy proxyStrategy;

    public UdpProxyServerFactory(ProxyStrategy proxyStrategy) {
        this.proxyStrategy = proxyStrategy;
    }

    @Override
    public ProxyServer newProxyServer(int port) {
        return new UdpProxyServer(port, proxyStrategy);
    }
}
