package nju.ics.platformserver.application;

import jakarta.annotation.Nonnull;
import nju.ics.platformserver.docker.DockerContainer;

import java.util.Map;
import java.util.UUID;

public final class Applications {
    private static final String PREFIX = "am-"; // for making identification in the Docker containers

    private static final String HEALTH_CHECK_PORT_LABEL_KEY = "health-check-port";

    private Applications() {
    }

    public static boolean isCreatedByApplicationManager(@Nonnull DockerContainer container) {
        return container.name().startsWith(PREFIX);
    }

    public static Application from(@Nonnull DockerContainer container) {
        return new Application(container.id(), container.image().name(), container.image().tag(),
                Integer.parseInt(container.labels().get(HEALTH_CHECK_PORT_LABEL_KEY)));
    }

    public static String generateContainerName() {
        return PREFIX + UUID.randomUUID();
    }

    public static Map<String, String> generateDefaultLabels(int healthCheckPort) {
        return Map.of(HEALTH_CHECK_PORT_LABEL_KEY, String.valueOf(healthCheckPort));
    }
}
