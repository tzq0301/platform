package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.util.*;
import java.util.stream.Collectors;

public class PubSub {
    protected final Map<Topic, Set<Client>> topicPublishers;

    protected final Map<Topic, Set<Client>> topicSubscribers;

    protected final Object lock; // used for synchronized

    private final List<PubSubPlugin> plugins;

    public PubSub() {
        this.topicPublishers = new HashMap<>();
        this.topicSubscribers = new HashMap<>();
        this.lock = new Object();
        this.plugins = new ArrayList<>();
    }

    public void registerClient(@Nonnull Client client, @Nonnull Set<Topic> pubTopics, @Nonnull Set<Topic> subTopics) {
        synchronized (lock) {
            pubTopics.forEach(pubTopic -> topicPublishers.computeIfAbsent(pubTopic, t -> new HashSet<>()).add(client));
            subTopics.forEach(subTopic -> topicSubscribers.computeIfAbsent(subTopic, t -> new HashSet<>()).add(client));
        }
    }

    public void unregisterClient(@Nonnull Client client) {
        synchronized (lock) {
            unregisterClient(client, topicPublishers);
            unregisterClient(client, topicSubscribers);
        }
    }

    private void unregisterClient(@Nonnull Client client, @Nonnull Map<Topic, Set<Client>> map) {
        synchronized (lock) {
            for (var iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
                var entry = iterator.next();
                entry.getValue().remove(client);
                if (entry.getValue().isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    public void registerPlugin(@Nonnull PubSubPlugin plugin) {
        synchronized (lock) {
            this.plugins.add(plugin);
            plugin.onRegister(this);
        }
    }

    public <T> void publish(@Nonnull Message<T> message) {
        synchronized (lock) {
            for (PubSubPlugin plugin : plugins) {
                plugin.beforeOnMessage(this, message);
            }

            Set<Client> subscribers = topicSubscribers.getOrDefault(message.topic(), Set.of());

            onMessage(subscribers, message);
        }
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

    public Map<Topic, Set<Client>> getTopicPublishers() {
        return cloneTopicClientsMap(topicPublishers);
    }

    public Map<Topic, Set<Client>> getTopicSubscribers() {
        Map<Topic, Set<Client>> topicPublishers = cloneTopicClientsMap(this.topicPublishers);
        Map<Topic, Set<Client>> topicSubscribers = cloneTopicClientsMap(this.topicSubscribers);
        this.plugins.forEach(plugin -> plugin.afterGetTopicSubscribers(topicPublishers, topicSubscribers));
        return topicSubscribers;
    }

    protected static Map<Topic, Set<Client>> cloneTopicClientsMap(@Nonnull Map<Topic, Set<Client>> topicClientsMap) {
        Map<Topic, Set<Client>> map = new HashMap<>();
        topicClientsMap.forEach((topic, clients) -> map.put(topic, new HashSet<>(clients)));
        return map;
    }
}
