package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

public interface PubSubPlugin {
    default void onRegister(@Nonnull PubSub pubsub) {
    }

    default void beforeOnMessage(@Nonnull PubSub pubsub, Message<?> message) {
    }

    default void afterGetPubSubStore(@Nonnull PubSubStore store) {
    }
}
