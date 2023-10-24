package nju.ics.platformmodel.pubsub;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ListUnreadMessagesRequest(@Nonnull @NotNull @NotEmpty String clientId) {
}
