package nju.ics.platformclientspringbootstarter.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

public record Message<T>(@Nonnull String id,
                         @Nonnull String publisherId,
                         @Nonnull Topic topic,
                         @Nullable T data,
                         @Nonnull LocalDateTime createTime) {
}
