package nju.ics.platformserver.application.model;

import jakarta.annotation.Nonnull;

public record DestroyApplicationCmd(@Nonnull String applicationId) {
}
