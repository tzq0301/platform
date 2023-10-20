package nju.ics.platformmodel.pubsub;

import jakarta.annotation.Nonnull;

public record PublishRequest(@Nonnull MessageDTO message) {
}
