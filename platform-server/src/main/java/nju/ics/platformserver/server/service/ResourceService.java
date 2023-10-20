package nju.ics.platformserver.server.service;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import nju.ics.platformmodel.resource.GraphResponse;
import nju.ics.platformserver.pubsub.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ResourceService {
    private final PubSub pubsub;

    private final AlivenessCheckerPlugin alivenessChecker;

    public ResourceService(@Nonnull PubSub pubsub,
                           @Nonnull AlivenessCheckerPlugin alivenessChecker) {
        this.pubsub = pubsub;
        this.alivenessChecker = alivenessChecker;
    }

    public Client register(@Nonnull String name, @Nonnull String type,
                           @Nonnull Set<Topic> pubTopics, @Nonnull Set<Topic> subTopics) {
        Client client = new Client(pubsub, name, type);
        pubsub.registerClient(client, pubTopics, subTopics);
        alivenessChecker.keepAlive(client);

        log.info("resource registered: {}", client);

        return client;
    }

    public void unregister(@Nonnull String clientId) {
        Client client = Clients.getById(clientId);
        pubsub.unregisterClient(client);

        log.info("resource unregistered: {}", client);
    }

    public GraphResponse graph() {
        Map<Topic, Set<Client>> topicPublishers = pubsub.getTopicPublishers();
        Map<Topic, Set<Client>> topicSubscribers = pubsub.getTopicSubscribers();

        Stream<Topic> allTopics = Stream.concat(
                topicPublishers.keySet().stream(),
                topicSubscribers.keySet().stream()
        ).distinct();

        Stream<Client> allClients = Stream.concat(
                topicPublishers.values().stream().flatMap(Set::stream),
                topicSubscribers.values().stream().flatMap(Set::stream)
        ).distinct();

        Set<GraphResponse.Node> nodes = Stream.concat(
                allTopics.map(topic -> new GraphResponse.Node("topic", topic.value(), topic.value())),
                allClients.map(client -> new GraphResponse.Node(client.getType(), client.getId(), client.getName()))
        ).collect(Collectors.toSet());

        Set<GraphResponse.Edge> edges = Stream.concat(
                topicPublishers.entrySet().stream().flatMap(entry -> {
                    Topic topic = entry.getKey();
                    Set<Client> publishers = entry.getValue();
                    return publishers.stream().map(publisher -> new GraphResponse.Edge(publisher.getId(), topic.value()));
                }),
                topicSubscribers.entrySet().stream().flatMap(entry -> {
                    Topic topic = entry.getKey();
                    Set<Client> subscribers = entry.getValue();
                    return subscribers.stream().map(subscriber -> new GraphResponse.Edge(topic.value(), subscriber.getId()));
                })
        ).collect(Collectors.toSet());

        return new GraphResponse(nodes, edges);
    }

    public void keepAlive(@Nonnull String clientId) {
        Client client = Clients.getById(clientId);
        alivenessChecker.keepAlive(client);
    }

    @Scheduled(cron = "*/30 * * * * ?")
    private void removeDeadClients() {
        try {
            alivenessChecker.removeDeadClients();
        } catch (NoSuchElementException ignored) {
        }
    }
}
