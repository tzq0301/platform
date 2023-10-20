package nju.ics.platformmodel.pubsub;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record MessageDTO(@Nonnull String id,
                         @Nonnull String publisherId,
                         @Nonnull String topic,
                         @Nullable Object data,
                         @Nonnull LocalDateTime createTime) {
}
