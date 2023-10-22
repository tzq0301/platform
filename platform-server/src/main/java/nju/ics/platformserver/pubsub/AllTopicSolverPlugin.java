package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class AllTopicSolverPlugin implements PubSubPlugin {
    private static final Topic ALL_TOPIC = new Topic("*");

    @Override
    public void beforeOnMessage(@Nonnull PubSub pubsub, @Nonnull Message<?> message) {
        Set<Client> subscribersToAllTopics = pubsub.store.subscribersOfTopic(ALL_TOPIC);
        pubsub.onMessage(subscribersToAllTopics, message);
    }

    @Override
    public void afterGetPubSubStore(@Nonnull PubSubStore store) {
        Set<Client> allTopicSubscribers = store.subscribersOfTopic(ALL_TOPIC);

        store.removeSubscriptionsOfTopic(ALL_TOPIC);

        var relations = new HashSet<RelationPair>();
        store.topics().forEach(topic -> relations.add(new RelationPair(Relation.SUBSCRIBE, topic)));
        allTopicSubscribers.forEach(client -> store.add(client, relations));
    }
}
