package nju.ics.platformserver.proxy.http;

import nju.ics.platformserver.net.Protocol;
import nju.ics.platformserver.proxy.ProxyServerManager;
import nju.ics.platformserver.proxy.strategy.GetLatestProxyStrategy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestProxyHTTP {
    @Test
    @Disabled
    void test() throws InterruptedException {
        ProxyServerManager manager = new ProxyServerManager("127.0.0.1", new GetLatestProxyStrategy());
        manager.register(Protocol.TCP, 8000, 9001);

        Thread.sleep(1000000000000000000L);
    }

    @Test
    @Disabled
    void testStrategy() throws InterruptedException {
        ProxyServerManager manager = new ProxyServerManager("127.0.0.1", new GetLatestProxyStrategy());
        manager.register(Protocol.TCP, 8000, 9001);
        manager.register(Protocol.TCP, 8000, 9002);

        Thread.sleep(1000000000000000000L);
    }
}
