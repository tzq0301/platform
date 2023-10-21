package nju.ics.platformserver.docker;

import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import jakarta.annotation.Nonnull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record DockerContainer(@Nonnull String id,
                              @Nonnull String name,
                              @Nonnull DockerImage image,
                              @Nonnull LocalDateTime createTime,
                              @Nonnull Set<DockerPortBinding> portBindings,
                              @Nonnull Map<String, String> labels) {
    public DockerContainer(@Nonnull Container container) {
        this(container.getId(), convertName(container.getNames()),
                convertDockerImage(container.getImage()), convertCreatTime(container.getCreated()),
                convertDockerPortBindings(container.getPorts()), container.getLabels());
    }

    private static final String SLASH = "/";

    private static final String COLON = ":";

    private static String convertName(@Nonnull String[] names) {
        String name = names[0];
        if (name.startsWith(SLASH)) {
            name = name.substring(SLASH.length());
        }

        return name;
    }

    private static LocalDateTime convertCreatTime(long created) {
        return LocalDateTime.ofEpochSecond(created, 0, ZoneOffset.UTC);
    }

    private static DockerImage convertDockerImage(@Nonnull String rawImage) {
        int indexOfColon = rawImage.indexOf(COLON);

        if (indexOfColon == -1) {
            return new DockerImage(rawImage, DockerImage.DEFAULT_TAG);
        }

        String name = rawImage.substring(0, indexOfColon);
        String tag = rawImage.substring(indexOfColon + 1);

        return new DockerImage(name, tag);
    }

    private static Set<DockerPortBinding> convertDockerPortBindings(ContainerPort[] ports) {
        return Arrays.stream(ports)
                .map(port -> {
                    Objects.requireNonNull(port);
                    Objects.requireNonNull(port.getType());
                    Objects.requireNonNull(port.getPublicPort());
                    Objects.requireNonNull(port.getPrivatePort());
                    return new DockerPortBinding(port.getType(), port.getPublicPort(), port.getPrivatePort());
                })
                .collect(Collectors.toSet());
    }
}
