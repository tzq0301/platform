package nju.ics.platformmodel.pubsub;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

public record PublishRequest(@Nonnull @NotNull MessageDTO message) {
}
