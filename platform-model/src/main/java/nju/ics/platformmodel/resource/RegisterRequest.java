package nju.ics.platformmodel.resource;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RegisterRequest(@Nonnull @NotNull @NotEmpty String clientName,
                              @Nonnull @NotNull @NotEmpty String clientType,
                              @Nonnull @NotNull Set<String> pubTopics,
                              @Nonnull @NotNull Set<String> subTopics) {
}
