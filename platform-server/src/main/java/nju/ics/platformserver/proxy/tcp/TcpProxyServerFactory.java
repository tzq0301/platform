package nju.ics.platformserver.proxy.tcp;

import nju.ics.platformserver.proxy.ProxyServer;
import nju.ics.platformserver.proxy.ProxyServerFactory;
import nju.ics.platformserver.proxy.strategy.ProxyStrategy;

public class TcpProxyServerFactory implements ProxyServerFactory {
    private final ProxyStrategy proxyStrategy;

    public TcpProxyServerFactory(ProxyStrategy proxyStrategy) {
        this.proxyStrategy = proxyStrategy;
    }

    @Override
    public ProxyServer newProxyServer(int port) {
        return new TcpProxyServer(port, proxyStrategy);
    }
}
