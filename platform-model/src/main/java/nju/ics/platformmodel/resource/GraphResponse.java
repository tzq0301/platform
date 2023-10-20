package nju.ics.platformmodel.resource;

import jakarta.annotation.Nonnull;

import java.util.Set;

public record GraphResponse(@Nonnull Set<Node> nodes, @Nonnull Set<Edge> edges) {
    public record Node(@Nonnull String clientType,
                       @Nonnull String id /* if clientType == topic, then use topic.name as the id */,
                       @Nonnull String name) {
    }

    public record Edge(@Nonnull String from /* id */,
                       @Nonnull String to /* id */) {
    }
}
