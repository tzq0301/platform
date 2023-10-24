package nju.ics.platformmodel.resource;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record KeepAliveRequest(@Nonnull @NotNull @NotEmpty String clientId) {
}
