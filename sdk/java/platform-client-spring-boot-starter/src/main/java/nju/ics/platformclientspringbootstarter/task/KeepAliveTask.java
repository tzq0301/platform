package nju.ics.platformclientspringbootstarter.task;

import jakarta.annotation.Nonnull;
import nju.ics.platformclientspringbootstarter.client.PlatformClient;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class KeepAliveTask implements Task {
    private final TaskScheduler taskScheduler;

    private final PlatformClient platformClient;

    private ScheduledFuture<?> schedule;

    public KeepAliveTask(@Nonnull TaskScheduler taskScheduler,
                         @Nonnull PlatformClient platformClient) {
        this.taskScheduler = taskScheduler;
        this.platformClient = platformClient;
    }

    @Override
    public void start() {
        this.schedule = this.taskScheduler.schedule(platformClient::keepAlive, new CronTrigger("*/3 * * * * ?"));
    }

    @Override
    public void stop() {
        this.schedule.cancel(false);
    }
}
