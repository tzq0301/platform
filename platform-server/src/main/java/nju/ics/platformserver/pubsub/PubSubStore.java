package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class PubSubStore implements Cloneable {
    private final Map<Client, Set<RelationPair>> store;

    public PubSubStore() {
        this.store = new HashMap<>();
    }

    public synchronized void add(@Nonnull Client client, @Nonnull Set<RelationPair> pubsubRelations) {
        store.computeIfAbsent(client, v -> new HashSet<>()).addAll(pubsubRelations);
    }

    public synchronized void remove(@Nonnull Client client) {
        store.remove(client);
    }

    @Nonnull
    public synchronized Set<Client> subscribersOfTopic(@Nonnull Topic topic) {
        return store.entrySet()
                .stream()
                .filter(entry -> {
                    var relations = entry.getValue();
                    for (var relation : relations) {
                        if (isSubscriptionOfTopic(relation, topic)) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Nonnull
    public synchronized Set<Topic> topics() {
        return store.entrySet()
                .stream()
                .flatMap(entry -> entry.getValue()
                        .stream()
                        .map(RelationPair::topic))
                .collect(Collectors.toSet());
    }

    @Nonnull
    public synchronized Set<Client> clients() {
        return store.keySet();
    }

    @Nonnull
    public synchronized Set<Pair<Pair<Client, Topic>, Relation>> relations() {
        Set<Pair<Pair<Client, Topic>, Relation>> relations = new HashSet<>();

        store.forEach((client, relationPairs) -> relationPairs.forEach(pair -> {
            relations.add(Pair.of(Pair.of(client, pair.topic()), pair.relation()));
        }));

        return relations;
    }

    public synchronized void removeSubscriptionsOfTopic(@Nonnull Topic topic) {
        store.forEach((client, relationPairs) -> relationPairs.removeIf(pair -> isSubscriptionOfTopic(pair, topic)));
    }

    @Override
    @Nonnull
    public synchronized PubSubStore clone() {
        PubSubStore cloned = new PubSubStore();
        this.store.forEach((client, relationPairs) -> cloned.store.put(client, new HashSet<>(relationPairs)));
        return cloned;
    }

    private static boolean isSubscriptionOfTopic(RelationPair pair, Topic topic) {
        return Objects.equals(pair.relation(), Relation.SUBSCRIBE)
                && Objects.equals(pair.topic(), topic);
    }
}
