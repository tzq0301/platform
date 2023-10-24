package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CreateApplicationRequest(@Nonnull @NotNull @NotEmpty String name,
                                       @Nonnull @NotNull @NotEmpty String version,
                                       @Nonnull @NotNull Integer healthCheckPort,
                                       @Nullable Set<Integer> udpPorts) {
}
