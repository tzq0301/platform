package nju.ics.platformserver.proxy.strategy;

import nju.ics.platformserver.proxy.TargetServer;

import java.util.Comparator;
import java.util.Set;

public class GetLatestProxyStrategy implements ProxyStrategy {
    @Override
    public TargetServer select(Set<TargetServer> targetServers) {
        return targetServers.stream()
                .max(Comparator.comparing(TargetServer::getRegisterTime))
                .orElseThrow();
    }
}
