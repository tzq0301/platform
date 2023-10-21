package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;

public record CreateApplicationResponse(@Nonnull ApplicationDTO application) {
    public CreateApplicationResponse(@Nonnull String id, @Nonnull String name, @Nonnull String version) {
        this(new ApplicationDTO(id, name, version));
    }
}
