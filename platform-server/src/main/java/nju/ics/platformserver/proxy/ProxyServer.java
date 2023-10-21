package nju.ics.platformserver.proxy;

public interface ProxyServer {
    void startAsDaemon();

    void stop();

    void addTargetServer(TargetServer targetServer);

    void removeTargetServer(TargetServer targetServer);
}
