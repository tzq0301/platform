package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;

public record RelationPair(@Nonnull Relation relation, @Nonnull Topic topic) {
}
