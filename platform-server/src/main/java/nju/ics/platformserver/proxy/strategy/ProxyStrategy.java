package nju.ics.platformserver.proxy.strategy;

import nju.ics.platformserver.proxy.TargetServer;

import java.util.Set;

public interface ProxyStrategy {
    TargetServer select(Set<TargetServer> targetServers);
}
