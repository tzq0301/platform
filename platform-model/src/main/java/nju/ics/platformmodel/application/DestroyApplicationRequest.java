package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DestroyApplicationRequest(@Nonnull @NotEmpty @NotNull String applicationId) {
}
