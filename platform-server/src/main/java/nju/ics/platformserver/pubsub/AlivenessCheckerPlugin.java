package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlivenessCheckerPlugin implements PubSubPlugin {
    private final Map<Client, LocalDateTime> alivenessRecorder;

    private final Duration maxHeartBeatInterval;

    private PubSub pubsub;

    public AlivenessCheckerPlugin(@Nonnull Duration maxHeartBeatInterval) {
        this.alivenessRecorder = new HashMap<>();
        this.maxHeartBeatInterval = maxHeartBeatInterval;
    }

    public void keepAlive(@Nonnull Client client) {
        synchronized (pubsub.lock) {
            alivenessRecorder.put(client, LocalDateTime.now());
        }
    }

    public List<Client> removeDeadClients() {
        synchronized (pubsub.lock) {
            LocalDateTime now = LocalDateTime.now();

            List<Client> tobeRemoved = new ArrayList<>();

            for (var iterator = alivenessRecorder.entrySet().iterator(); iterator.hasNext(); ) {
                var entry = iterator.next();

                Client client = entry.getKey();
                LocalDateTime lastHeartBeatTime = entry.getValue();

                if (lastHeartBeatTime.plus(this.maxHeartBeatInterval).isAfter(now)) {
                    continue;
                }

                tobeRemoved.add(client);
                iterator.remove();
            }

            tobeRemoved.forEach(pubsub::unregisterClient);

            return tobeRemoved;
        }
    }

    @Override
    public void onRegister(@Nonnull PubSub pubsub) {
        this.pubsub = pubsub;
    }
}
