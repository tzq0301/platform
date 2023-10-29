package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Clients {
    private static final Map<String, Client> store = new HashMap<>();

    private Clients() {
    }

    public static void add(@Nonnull Client client) {
        store.put(client.getId(), client);
    }

    @Nonnull
    public static Client getById(@Nonnull String id) {
        return Objects.requireNonNull(
                store.get(id),
                String.format("Client 不存在（请确认该 Client 是否是本次 Platform 启动过程中创建的）[id = %s]", id));
    }
}
