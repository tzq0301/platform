package nju.ics.platformserver.application.model;

import jakarta.annotation.Nonnull;

import java.util.List;

public record CreateApplicationCmd(@Nonnull String name,
                                   @Nonnull String version,
                                   int healthCheckPort,
                                   @Nonnull List<Integer> exposedTcpPorts,
                                   @Nonnull List<Integer> exposedUdpPorts) {
}
