package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;

public record CreateApplicationRequest(@Nonnull String name, @Nonnull String version, int healthCheckPort) {
}
