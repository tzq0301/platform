package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.Set;

public interface PubSubPlugin {
    default void onRegister(@Nonnull PubSub pubsub) {
    }

    default void beforeOnMessage(@Nonnull PubSub pubsub, Message<?> message) {
    }

    default void afterGetTopicSubscribers(@Nonnull Map<Topic, Set<Client>> topicPublishers,
                                          @Nonnull Map<Topic, Set<Client>> topicSubscribers) {
    }
}
