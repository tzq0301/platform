package nju.ics.platformclientspringbootstarter.task;

import jakarta.annotation.Nonnull;
import nju.ics.platformclientspringbootstarter.client.PlatformClient;
import nju.ics.platformclientspringbootstarter.handler.HandlerInstance;
import nju.ics.platformclientspringbootstarter.handler.HandlerRegistry;
import nju.ics.platformclientspringbootstarter.model.Message;
import nju.ics.platformclientspringbootstarter.model.Topic;
import nju.ics.platformclientspringbootstarter.publish.Publisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

@Component
public class ListUnreadMessagesTask implements Task {
    private final TaskScheduler taskScheduler;

    private final HandlerRegistry handlerRegistry;

    private final PlatformClient platformClient;

    private final Publisher publisher;

    private ScheduledFuture<?> schedule;

    public ListUnreadMessagesTask(@Nonnull final TaskScheduler taskScheduler,
                                  @Nonnull final HandlerRegistry handlerRegistry,
                                  @Nonnull final PlatformClient platformClient,
                                  @Nonnull final Publisher publisher) {
        this.taskScheduler = Objects.requireNonNull(taskScheduler);
        this.handlerRegistry = Objects.requireNonNull(handlerRegistry);
        this.platformClient = Objects.requireNonNull(platformClient);
        this.publisher = Objects.requireNonNull(publisher);
    }

    @Override
    public void start() {
        this.schedule = taskScheduler.schedule(() -> platformClient.listUnreadMessagesResponse()
                .forEach(message -> {
                    try {
                        HandlerInstance handler = handlerRegistry.getMethod(new Topic(message.topic()));

                        List<Object> params = new ArrayList<>();
                        for (Class<?> parameterType : handler.method().getParameterTypes()) {
                            if (Message.class.equals(parameterType)) {
                                params.add(new Message<>(message.id(), message.publisherId(), new Topic(message.topic()), message.data(), message.createTime()));
                            } else if (Publisher.class.equals(parameterType)) {
                                params.add(publisher);
                            }
                        }

                        handler.method().invoke(handler.obj(), params.toArray());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }), new CronTrigger("*/1 * * * * ?"));
    }

    @Override
    public void stop() {
        schedule.cancel(false);
    }
}
