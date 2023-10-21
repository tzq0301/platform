package nju.ics.platformserver.application;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;

/**
 * A continually-running service, with an exposed port for health checking
 */
public record Application(@Nonnull String id, @Nonnull String name, @Nonnull String version,
                          @JsonIgnore int healthCheckPort) {
}
