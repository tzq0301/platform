package nju.ics.platformserver.server.controller;

import jakarta.annotation.Nonnull;
import nju.ics.platformmodel.resource.*;
import nju.ics.platformserver.pubsub.Client;
import nju.ics.platformserver.pubsub.Topic;
import nju.ics.platformserver.server.service.ResourceService;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    private final ResourceService resourceService;

    public ResourceController(@Nonnull ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@Nonnull @RequestBody RegisterRequest request) {
        var pubTopics = request.pubTopics().stream().map(Topic::new).collect(Collectors.toSet());
        var subTopics = request.subTopics().stream().map(Topic::new).collect(Collectors.toSet());
        Client client = resourceService.register(request.clientName(), request.clientType(), pubTopics, subTopics);
        return new RegisterResponse(client.getId());
    }

    @PostMapping("/unregister")
    public void unregister(@Nonnull @RequestBody UnregisterRequest request) {
        resourceService.unregister(request.clientId());
    }

    @GetMapping("/graph")
    public GraphResponse graph() {
        return resourceService.graph();
    }

    @PostMapping("/keepAlive")
    public void keepAlive(@Nonnull @RequestBody KeepAliveRequest request) {
        resourceService.keepAlive(request.clientId());
    }
}
