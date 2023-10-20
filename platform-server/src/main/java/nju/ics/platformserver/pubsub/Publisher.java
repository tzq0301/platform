package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@FunctionalInterface
public interface Publisher {
    <T> void publish(@Nonnull Topic topic, @Nullable T data);
}
