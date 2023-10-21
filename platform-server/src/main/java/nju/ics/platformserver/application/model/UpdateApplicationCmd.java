package nju.ics.platformserver.application.model;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.application.update.UpdateStrategy;

public record UpdateApplicationCmd(@Nonnull CreateApplicationCmd createApplicationCmd,
                                   @Nonnull DestroyApplicationCmd destroyApplicationCmd,
                                   @Nonnull UpdateStrategy updateStrategy) {
}
