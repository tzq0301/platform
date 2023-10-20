package nju.ics.platformserver.server.service;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.pubsub.Client;
import nju.ics.platformserver.pubsub.Clients;
import nju.ics.platformserver.pubsub.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PubSubService {
    public <T> void publish(@Nonnull Message<T> message) {
        Client client = Clients.getById(message.publisherId());
        client.publish(message);
    }

    public List<Message<?>> listUnreadMessages(@Nonnull String clientId) {
        Client client = Clients.getById(clientId);
        return client.listUnreadMessages();
    }
}
