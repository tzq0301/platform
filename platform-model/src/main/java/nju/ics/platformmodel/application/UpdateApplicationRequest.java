package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UpdateApplicationRequest(@Nonnull @NotNull @NotEmpty String oldApplicationId,
                                       @Nonnull @NotNull @NotEmpty String newApplicationName,
                                       @Nonnull @NotNull @NotEmpty String newApplicationVersion,
                                       @Nonnull @NotNull Integer newApplicationHealthCheckPort,
                                       @Nullable Set<Integer> newApplicationUdpPorts,
                                       @Nonnull @NotNull UpdateStrategyEnum updateStrategy) {
}
