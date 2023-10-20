package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class AllTopicSolverPlugin implements PubSubPlugin {
    private static final Topic ALL_TOPIC = new Topic("*");

    @Override
    public void beforeOnMessage(@Nonnull PubSub pubsub, @Nonnull Message<?> message) {
        Set<Client> subscribersToAllTopics = pubsub.topicSubscribers.getOrDefault(ALL_TOPIC, Set.of());
        pubsub.onMessage(subscribersToAllTopics, message);
    }

    @Override
    public void afterGetTopicSubscribers(@Nonnull Map<Topic, Set<Client>> topicPublishers,
                                         @Nonnull Map<Topic, Set<Client>> topicSubscribers) {
        Set<Client> allTopicSubscribers = topicSubscribers.getOrDefault(ALL_TOPIC, Set.of());
        topicSubscribers.remove(ALL_TOPIC);

        allTopicSubscribers
                .forEach(client -> new HashSet<Topic>() {{
                    addAll(topicPublishers.keySet());
                    addAll(topicSubscribers.keySet());
                }}.forEach(topic -> topicSubscribers.computeIfAbsent(topic, t -> new HashSet<>()).add(client)));
    }
}
