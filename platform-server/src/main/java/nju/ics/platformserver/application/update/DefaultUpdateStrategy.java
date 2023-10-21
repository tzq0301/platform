package nju.ics.platformserver.application.update;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.application.Application;

public class DefaultUpdateStrategy implements UpdateStrategy {
    @Override
    public Application update(@Nonnull Creator applicationCreator, @Nonnull Destroyer applicationDestroyer) {
        applicationDestroyer.destroy();
        return applicationCreator.create();
    }
}
