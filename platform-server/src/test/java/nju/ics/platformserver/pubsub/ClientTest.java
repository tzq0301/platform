package nju.ics.platformserver.pubsub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientTest {

    @Test
    void listUnreadMessages() {
        Client client = new Client(new PubSub(), "client", "client");

        assertEquals(0, client.listUnreadMessages().size());

        client.onMessage(new Message<>("", new Topic(""), null));

        assertEquals(1, client.listUnreadMessages().size());

        client.onMessage(new Message<>("", new Topic(""), null));
        client.onMessage(new Message<>("", new Topic(""), null));
        client.onMessage(new Message<>("", new Topic(""), null));

        assertEquals(3, client.listUnreadMessages().size());
        assertEquals(0, client.listUnreadMessages().size());
    }
}