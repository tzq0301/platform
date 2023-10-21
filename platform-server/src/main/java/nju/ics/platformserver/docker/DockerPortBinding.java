package nju.ics.platformserver.docker;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.net.Protocol;

public record DockerPortBinding(@Nonnull Protocol protocol, int publicPort, int privatePort) {
    public DockerPortBinding(@Nonnull String protocol, int publicPort, int privatePort) {
        this(convertProtocol(protocol), publicPort, privatePort);
    }

    private static Protocol convertProtocol(@Nonnull String rawProtocol) {
        return switch (rawProtocol.toUpperCase()) {
            case "TCP" -> Protocol.TCP;
            case "UDP" -> Protocol.UDP;
            default -> throw new IllegalStateException("Unexpected value: " + rawProtocol.toUpperCase());
        };
    }
}
