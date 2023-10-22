package nju.ics.platformserver.server.service;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import nju.ics.platformmodel.resource.GraphResponse;
import nju.ics.platformserver.pubsub.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
        PubSubStore store = pubsub.getPubSubStore();

        Set<GraphResponse.Node> nodes = Stream.concat(
                store.clients().stream().map(client -> new GraphResponse.Node(client.getType(), client.getId(), client.getName())),
                store.topics().stream().map(topic -> new GraphResponse.Node("topic", topic.value(), topic.value()))
        ).collect(Collectors.toSet());

        Set<GraphResponse.Edge> edges = store.relations()
                .stream()
                .map(pair -> switch (pair.getRight()) {
                    case PUBLISH -> new GraphResponse.Edge(pair.getLeft().getLeft().getId(), pair.getLeft().getRight().value());
                    case SUBSCRIBE -> new GraphResponse.Edge(pair.getLeft().getRight().value(), pair.getLeft().getLeft().getId());
                })
                .collect(Collectors.toSet());

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
