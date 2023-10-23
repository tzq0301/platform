package nju.ics.platformserver.application;

import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.exception.NotFoundException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import nju.ics.platformserver.application.model.CreateApplicationCmd;
import nju.ics.platformserver.application.model.DestroyApplicationCmd;
import nju.ics.platformserver.application.model.UpdateApplicationCmd;
import nju.ics.platformserver.docker.DockerManager;
import nju.ics.platformserver.docker.DockerPortBinding;
import nju.ics.platformserver.net.Protocol;
import nju.ics.platformserver.proxy.ProxyServerManager;
import nju.ics.platformserver.server.config.ApplicationManagerProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ApplicationManager {
    private static final Duration TEST_CREATE_APPLICATION_SUCCESS_RETRY_INTERVAL = Duration.ofSeconds(1L);

    private final ProxyServerManager proxyServerManager;

    private final DockerManager dockerManager;

    private final RandomUnusedPortsManager randomUnusedPortsManager;

    private final ApplicationManagerProperties applicationManagerProperties;

    public ApplicationManager(@Nonnull ProxyServerManager proxyServerManager,
                              @Nonnull DockerManager dockerManager,
                              @Nonnull ApplicationManagerProperties applicationManagerProperties) {
        this.proxyServerManager = proxyServerManager;
        this.dockerManager = dockerManager;
        this.randomUnusedPortsManager = new RandomUnusedPortsManager(12000, 13000);
        this.applicationManagerProperties = applicationManagerProperties;
    }

    public Application createApplication(@Nonnull CreateApplicationCmd cmd) {
        final DockerPortBinding healthCheckPortBinding = new DockerPortBinding(Protocol.TCP, randomUnusedPortsManager.generate(), cmd.healthCheckPort());
        final Set<DockerPortBinding> portBindings = Stream.concat(
                        cmd.exposedTcpPorts().stream() // TCPs
                                .distinct()
                                .map(tcpPort -> new DockerPortBinding(Protocol.TCP, randomUnusedPortsManager.generate(), tcpPort)),
                        cmd.exposedUdpPorts().stream()  // UDPs
                                .distinct()
                                .map(udpPort -> new DockerPortBinding(Protocol.UDP, randomUnusedPortsManager.generate(), udpPort)))
                .collect(Collectors.toSet());
        portBindings.add(healthCheckPortBinding);

        final String imageName = cmd.name();
        final String imageTag = cmd.version();

        try {
            dockerManager.pullImage(imageName, imageTag);
        } catch (NotFoundException e) {
            log.info("can not found {}:{} on docker hub, try using local image", imageName, imageTag);
        } catch (InternalServerErrorException e) {
            log.error("please try again: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        final String containerName = Applications.generateContainerName();
        final Map<String, String> containerLabels = Applications.generateDefaultLabels(cmd.healthCheckPort());

        String containerId = dockerManager.createContainer(imageName, imageTag, containerName, portBindings, containerLabels);
        dockerManager.startContainer(containerId);

        final Application application = Applications.from(dockerManager.getContainerById(containerId));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .GET()
                            .uri(new URI(String.format("http://%s:%s/platform/client/status/ready",
                                    applicationManagerProperties.dockerUri().getHost(),
                                    healthCheckPortBinding.publicPort())))
                            .build();

                    HttpClient client = HttpClient.newBuilder()
                            .version(HttpClient.Version.HTTP_1_1)
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (Boolean.TRUE.equals(Boolean.parseBoolean(response.body()))) {
                        timer.cancel();
                    }
                } catch (IOException | InterruptedException | URISyntaxException ignored) {
                }
            }
        }, 0L, TEST_CREATE_APPLICATION_SUCCESS_RETRY_INTERVAL.toMillis());

        portBindings.forEach(binding -> {
            int proxyPort = binding.privatePort();
            int actualPort = binding.publicPort();
            Protocol protocol = switch (binding.protocol()) {
                case TCP -> Protocol.TCP;
                case UDP -> Protocol.UDP;
            };
            proxyServerManager.register(protocol, proxyPort, actualPort);
        });

        return application;
    }

    public void destroyApplication(@Nonnull DestroyApplicationCmd cmd) {
        final var containerId = cmd.applicationId();

        final var container = dockerManager.getContainerById(containerId);

        container.portBindings().forEach(binding -> {
            int proxyPort = binding.privatePort();
            int actualPort = binding.publicPort();
            proxyServerManager.unregister(proxyPort, actualPort);
            randomUnusedPortsManager.makeUnused(actualPort);
        });

        dockerManager.stopContainer(container.id());
        dockerManager.removeContainer(container.id());
    }

    public Application updateApplication(@Nonnull UpdateApplicationCmd cmd) {
        return cmd.updateStrategy().update(
                () -> createApplication(cmd.createApplicationCmd()),
                () -> destroyApplication(cmd.destroyApplicationCmd()));
    }

    public List<Application> listApplications() {
        return dockerManager.listContainers()
                .stream()
                .filter(Applications::isCreatedByApplicationManager)
                .map(Applications::from)
                .toList();
    }
}
