package nju.ics.platformmodel.pubsub;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MessageDTO(@Nonnull @NotNull @NotEmpty String id,
                         @Nonnull @NotNull @NotEmpty String publisherId,
                         @Nonnull @NotNull @NotEmpty String topic,
                         @Nullable Object data,
                         @Nonnull @NotNull LocalDateTime createTime) {
}
