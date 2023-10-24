package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PubSub {
    protected final PubSubStore store;

    protected final Object lock; // used for synchronized

    private final List<PubSubPlugin> plugins;

    public PubSub() {
        this.store = new PubSubStore();
        this.lock = new Object();
        this.plugins = new CopyOnWriteArrayList<>();
    }

    public void registerClient(@Nonnull Client client, @Nonnull Set<Topic> pubTopics, @Nonnull Set<Topic> subTopics) {
        Set<RelationPair> pubsubRelations = Collections.newSetFromMap(new ConcurrentHashMap<>());

        pubTopics.forEach(topic -> pubsubRelations.add(new RelationPair(Relation.PUBLISH, topic)));
        subTopics.forEach(topic -> pubsubRelations.add(new RelationPair(Relation.SUBSCRIBE, topic)));

        store.add(client, pubsubRelations);
    }

    public void unregisterClient(@Nonnull Client client) {
        store.remove(client);
    }

    public void registerPlugin(@Nonnull PubSubPlugin plugin) {
        this.plugins.add(plugin);
        plugin.onRegister(this);
    }

    public <T> void publish(@Nonnull Message<T> message) {
        for (PubSubPlugin plugin : plugins) {
            plugin.beforeOnMessage(this, message);
        }

        Set<Client> subscribers = store.subscribersOfTopic(message.topic());

        onMessage(subscribers, message);
    }

    protected void onMessage(@Nonnull Set<Client> subscribers, @Nonnull Message<?> message) {
        subscribers.stream()
                .collect(Collectors.groupingBy(Client::getName))
                .values()
                .stream()
                .flatMap(clients -> clients.stream()
                        .sorted(Comparator.comparing(Client::getCreateTime).reversed())
                        .limit(1L))
                .forEach(client -> client.onMessage(message));
    }

    public PubSubStore getPubSubStore() {
        PubSubStore cloned = this.store.clone();
        this.plugins.forEach(plugin -> plugin.afterGetPubSubStore(cloned));
        return cloned;
    }
}
