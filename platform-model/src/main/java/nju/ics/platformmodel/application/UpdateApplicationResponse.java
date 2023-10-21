package nju.ics.platformmodel.application;

import jakarta.annotation.Nonnull;

public record UpdateApplicationResponse(@Nonnull ApplicationDTO application) {
    public UpdateApplicationResponse(@Nonnull String id, @Nonnull String name, @Nonnull String version) {
        this(new ApplicationDTO(id, name, version));
    }
}
