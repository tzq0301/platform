package nju.ics.platformserver.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import jakarta.annotation.Nonnull;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DockerManager {
    private final DockerClient dockerClient;

    public DockerManager(@Nonnull URI dockerUri) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerUri.getHost())
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerUri)
                .build();

        var dockerClient = DockerClientImpl.getInstance(config, httpClient);

        this.dockerClient = dockerClient;
    }

    public void pullImage(@Nonnull String imageName, @Nonnull String imageTag) throws NotFoundException {
        try {
            dockerClient
                    .pullImageCmd(imageName)
                    .withTag(imageTag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String createContainer(@Nonnull String imageName, @Nonnull String imageTag,
                                  @Nonnull String containerName,
                                  @Nonnull Set<DockerPortBinding> containerPortBindings,
                                  @Nonnull Map<String, String> containerLabels) {
        Ports portBindings = new Ports();
        List<ExposedPort> exposedPorts = containerPortBindings.stream()
                .map(binding -> {
                    var exposedPort = switch (binding.protocol()) {
                        case TCP -> ExposedPort.tcp(binding.privatePort());
                        case UDP -> ExposedPort.udp(binding.privatePort());
                    };
                    portBindings.bind(exposedPort, Ports.Binding.bindPort(binding.publicPort()));
                    return exposedPort;
                })
                .toList();

        return dockerClient
                .createContainerCmd(String.format("%s:%s", imageName, imageTag))
                .withName(containerName)
                .withExposedPorts(exposedPorts)
                .withHostConfig(HostConfig.newHostConfig()
                        .withPortBindings(portBindings))
                .withLabels(containerLabels)
                .withTty(true)
                .exec()
                .getId();
    }

    public void startContainer(@Nonnull String containerId) {
        dockerClient
                .startContainerCmd(containerId)
                .exec();
    }

    public void stopContainer(@Nonnull String containerId) {
        dockerClient
                .stopContainerCmd(containerId)
                .exec();
    }

    public void removeContainer(@Nonnull String containerId) {
        dockerClient
                .removeContainerCmd(containerId)
                .exec();
    }

    public List<DockerContainer> listContainers() {
        return dockerClient
                .listContainersCmd()
                .exec()
                .stream()
                .map(DockerContainer::new)
                .toList();
    }

    public DockerContainer getContainerById(@Nonnull String containerId) {
        return dockerClient
                .listContainersCmd()
                .exec()
                .stream()
                .filter(container -> Objects.equals(containerId, container.getId()))
                .findAny()
                .map(DockerContainer::new)
                .orElseThrow();
    }
}
