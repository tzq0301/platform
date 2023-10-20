package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

@FunctionalInterface
public interface Subscriber {
    <T> void onMessage(@Nonnull Message<T> message);
}
