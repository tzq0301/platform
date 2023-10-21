package nju.ics.platformserver.docker;

import nju.ics.platformserver.net.Protocol;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DockerManagerTest {
    @Test
    @Disabled
    void test() throws URISyntaxException {
        String dockerHost = "tcp://localhost:2375";
        URI dockerUri = new URI(dockerHost);

        DockerManager manager = new DockerManager(dockerUri);

        manager.pullImage("boxboat/hello-world-webapp", "latest");

        String containerId = manager.createContainer("boxboat/hello-world-webapp", "latest", "helloworld",
                Set.of(new DockerPortBinding(Protocol.TCP, 8080, 8080),
                        new DockerPortBinding(Protocol.TCP, 8081, 8081)),
                Map.of("hello", "world"));

        manager.startContainer(containerId);

        DockerContainer container = manager.getContainerById(containerId);

        assertEquals(containerId, container.id());
        assertEquals("boxboat/hello-world-webapp", container.image().name());
        assertEquals("latest", container.image().tag());
        assertEquals(2, container.portBindings().size());
        assertEquals("world", container.labels().get("hello"));

        List<DockerContainer> containers = manager.listContainers().stream()
                .filter(c -> Objects.equals(c.id(), containerId))
                .toList();
        assertEquals(1, containers.size());

        assertEquals(containers.get(0), container);

        manager.stopContainer(containerId);
        manager.removeContainer(containerId);

        assertTrue(manager.listContainers()
                .stream()
                .filter(c -> Objects.equals(c.id(), containerId))
                .toList()
                .isEmpty());
    }

    @Test
    @Disabled
    void testDocker() throws URISyntaxException {
        String dockerHost = "tcp://localhost:2375";
        URI dockerUri = new URI(dockerHost);

        DockerManager manager = new DockerManager(dockerUri);

        manager.listContainers().forEach(System.out::println);
    }
}