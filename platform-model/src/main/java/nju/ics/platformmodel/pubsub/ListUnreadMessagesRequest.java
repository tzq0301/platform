package nju.ics.platformmodel.pubsub;

import jakarta.annotation.Nonnull;

public record ListUnreadMessagesRequest(@Nonnull String clientId) {
}
