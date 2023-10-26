package nju.ics.platformclientspringbootstarter.processor;

import jakarta.annotation.Nonnull;
import nju.ics.platformclientspringbootstarter.event.ClientRegisterEvent;
import nju.ics.platformclientspringbootstarter.task.Task;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskExecutionProcessor {
    private final List<Task> tasks;

    public TaskExecutionProcessor(@Nonnull List<Task> tasks) {
        this.tasks = tasks;
    }

    @EventListener(ClientRegisterEvent.class)
    @Async
    protected void onClientRegisterSuccess() {
        tasks.forEach(Task::start);
    }
}
