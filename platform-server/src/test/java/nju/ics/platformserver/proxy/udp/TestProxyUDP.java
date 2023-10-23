package nju.ics.platformserver.proxy.udp;

import nju.ics.platformserver.net.Protocol;
import nju.ics.platformserver.proxy.ProxyServerManager;
import nju.ics.platformserver.proxy.strategy.GetLatestProxyStrategy;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TestProxyUDP {
    @Test
    @Disabled
    void test() throws InterruptedException {
        ProxyServerManager manager = new ProxyServerManager("localhost", new GetLatestProxyStrategy());
        manager.register(Protocol.UDP, 6000, 7000);

        Thread.sleep(1111111111111L);
    }

    @Test
    @Disabled
    void testClient() throws IOException {
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            new Thread(() -> {
                byte[] out = "Hello World".getBytes(StandardCharsets.UTF_8);
                DatagramPacket outPacket = null;
                try {
                    outPacket = new DatagramPacket(out, out.length, InetAddress.getLocalHost(), 7000);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                try {
                    udpSocket.send(outPacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            // ------------------

            byte[] in = new byte[1024];
            DatagramPacket inPacket = new DatagramPacket(in, in.length, InetAddress.getLocalHost(), 7000);
            while (true) {
                udpSocket.receive(inPacket);
                System.out.println(new String(inPacket.getData(), inPacket.getOffset(), inPacket.getLength()));
            }
        }
    }
}
