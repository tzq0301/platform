package nju.ics.platformserver.application.update;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.application.Application;


public interface UpdateStrategy {
    Application update(@Nonnull Creator applicationCreator, @Nonnull Destroyer applicationDestroyer);

    @FunctionalInterface
    interface Creator {
        Application create();
    }

    @FunctionalInterface
    interface Destroyer {
        void destroy();
    }
}
