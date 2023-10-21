package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;

public record ApplicationDTO(@Nonnull String id, @Nonnull String name, @Nonnull String version) {
}
