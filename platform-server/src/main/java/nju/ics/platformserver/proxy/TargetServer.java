package nju.ics.platformserver.proxy;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public final class TargetServer {
    private final String host;

    private final int port;

    private final LocalDateTime registerTime;

    public TargetServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.registerTime = LocalDateTime.now();
    }
}
