package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

public record Message<T>(@Nonnull String id,
                         @Nonnull String publisherId,
                         @Nonnull Topic topic,
                         @Nullable T data,
                         @Nonnull LocalDateTime createTime) {
    public Message(@Nonnull String publisherId, @Nonnull Topic topic, @Nullable T data) {
        this(UUID.randomUUID().toString(), publisherId, topic, data, LocalDateTime.now());
    }
}
