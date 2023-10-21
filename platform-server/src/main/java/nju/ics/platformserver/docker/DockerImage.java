package nju.ics.platformserver.docker;

import jakarta.annotation.Nonnull;

public record DockerImage(@Nonnull String name,
                          @Nonnull String tag) {
    public static final String DEFAULT_TAG = "latest";
}
