package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UpdateApplicationRequest(@Nonnull @NotNull @NotEmpty String oldApplicationId,
                                       @Nonnull @NotNull @NotEmpty String newApplicationName,
                                       @Nonnull @NotNull @NotEmpty String newApplicationVersion,
                                       @Nonnull @NotNull Integer newApplicationHealthCheckPort,
                                       @Nonnull @NotNull UpdateStrategyEnum updateStrategy) {
}
