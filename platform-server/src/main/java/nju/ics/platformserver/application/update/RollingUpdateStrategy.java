package nju.ics.platformserver.application.update;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.application.Application;

public class RollingUpdateStrategy implements UpdateStrategy {
    @Override
    public Application update(@Nonnull Creator applicationCreator, @Nonnull Destroyer applicationDestroyer) {
        Application application = applicationCreator.create();
        applicationDestroyer.destroy();
        return application;
    }
}
