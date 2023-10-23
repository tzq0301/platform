package nju.ics.platformserver.application.update;

import jakarta.annotation.Nonnull;
import lombok.SneakyThrows;
import nju.ics.platformserver.application.Application;

public class RollingUpdateStrategy implements UpdateStrategy {
    @SneakyThrows
    @Override
    public Application update(@Nonnull Creator applicationCreator, @Nonnull Destroyer applicationDestroyer) {
        Application application = applicationCreator.create();

        Thread.sleep(2_000); // 给甲方感受用

        applicationDestroyer.destroy();
        return application;
    }
}
