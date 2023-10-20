package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;

public final class Clients {
    private static final Map<String, Client> store = new HashMap<>();

    private Clients() {
    }

    public static void add(@Nonnull Client client) {
        store.put(client.getId(), client);
    }

    public static Client getById(@Nonnull String id) {
        return store.get(id);
    }
}
