package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;

public record UpdateApplicationRequest(@Nonnull String oldApplicationId,
                                       @Nonnull String newApplicationName,
                                       @Nonnull String newApplicationVersion,
                                       @Nonnull Integer newApplicationHealthCheckPort,
                                       @Nonnull UpdateStrategyEnum updateStrategy) {
}
