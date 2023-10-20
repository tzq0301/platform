package nju.ics.platformmodel.resource;

import jakarta.annotation.Nonnull;

import java.util.Set;

public record RegisterRequest(@Nonnull String clientName,
                              @Nonnull String clientType,
                              @Nonnull Set<String> pubTopics,
                              @Nonnull Set<String> subTopics) {
}
