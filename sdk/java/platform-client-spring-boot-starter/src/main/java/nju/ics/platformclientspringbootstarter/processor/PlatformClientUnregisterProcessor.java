package nju.ics.platformclientspringbootstarter.processor;

import jakarta.annotation.Nonnull;
import nju.ics.platformclientspringbootstarter.client.PlatformClient;
import nju.ics.platformclientspringbootstarter.task.Task;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PlatformClientUnregisterProcessor {
    private final PlatformClient platformClient;

    private final List<Task> tasks;

    public PlatformClientUnregisterProcessor(@Nonnull final PlatformClient platformClient,
                                  @Nonnull final List<Task> tasks) {
        this.platformClient = Objects.requireNonNull(platformClient);
        this.tasks = Objects.requireNonNull(tasks);
    }

    @EventListener(ContextClosedEvent.class)
    public void onShutdown() {
        tasks.forEach(Task::stop);
        platformClient.unregister();
    }
}
