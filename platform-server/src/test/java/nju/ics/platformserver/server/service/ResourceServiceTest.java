package nju.ics.platformserver.server.service;

import jakarta.annotation.Resource;
import nju.ics.platformmodel.resource.GraphResponse;
import nju.ics.platformserver.pubsub.Client;
import nju.ics.platformserver.pubsub.PubSub;
import nju.ics.platformserver.pubsub.Topic;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResourceServiceTest {

    @Resource
    ResourceService resourceService;

    @Test
    void graph() {
        Topic t1 = new Topic("t1");
        Topic t2 = new Topic("t2");
        Topic t3 = new Topic("t3");

        resourceService.register("pub", "pub", Set.of(t1, t2), Set.of());
        resourceService.register("sub", "sub", Set.of(), Set.of(t2, t3));

        {
            GraphResponse graph = resourceService.graph();
            assertEquals(5, graph.nodes().size());
            assertEquals(4, graph.edges().size());
        }

        resourceService.register("subAll", "sub", Set.of(), Set.of(new Topic("*")));

        {
            GraphResponse graph = resourceService.graph();
            assertEquals(6, graph.nodes().size());
            assertEquals(7, graph.edges().size());
        }

        Topic t4 = new Topic("t4");
        resourceService.register("pub2", "pub", Set.of(t4), Set.of());

        {
            GraphResponse graph = resourceService.graph();
            assertEquals(8, graph.nodes().size());
            assertEquals(9, graph.edges().size());
        }
    }
}