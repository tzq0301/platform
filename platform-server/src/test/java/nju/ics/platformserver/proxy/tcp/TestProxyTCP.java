package nju.ics.platformserver.proxy.tcp;

import nju.ics.platformserver.net.Protocol;
import nju.ics.platformserver.proxy.ProxyServerManager;
import nju.ics.platformserver.proxy.strategy.GetLatestProxyStrategy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TestProxyTCP {
    @Test
    @Disabled
    void test() throws InterruptedException {
        ProxyServerManager manager = new ProxyServerManager("127.0.0.1", new GetLatestProxyStrategy());
        manager.register(Protocol.TCP, 8000, 9010);

        Thread.sleep(1111111111111L);
    }
}
