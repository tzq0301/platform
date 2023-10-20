package nju.ics.platformclientspringbootstarter.processor;

import jakarta.annotation.Nonnull;
import nju.ics.platformclientspringbootstarter.client.PlatformClient;
import nju.ics.platformclientspringbootstarter.event.ClientRegisterEvent;
import nju.ics.platformclientspringbootstarter.handler.Handler;
import nju.ics.platformclientspringbootstarter.handler.HandlerRegistry;
import nju.ics.platformclientspringbootstarter.handler.Listener;
import nju.ics.platformclientspringbootstarter.model.Topic;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlatformClientRegisterProcessor {
    private final ApplicationContext applicationContext;

    private final ApplicationEventPublisher eventPublisher;

    private final HandlerRegistry handlerRegistry;

    private final PlatformClient platformClient;

    public PlatformClientRegisterProcessor(@Nonnull ApplicationContext applicationContext,
                                           @Nonnull ApplicationEventPublisher eventPublisher,
                                           @Nonnull HandlerRegistry handlerRegistry,
                                           @Nonnull PlatformClient platformClient) {
        this.applicationContext = applicationContext;
        this.eventPublisher = eventPublisher;
        this.handlerRegistry = handlerRegistry;
        this.platformClient = platformClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        Set<Topic> pubTopics = new HashSet<>(applicationContext.getBeansOfType(Topic.class).values());

        Set<Topic> subTopics = applicationContext.getBeansWithAnnotation(Listener.class).values().stream()
                .flatMap(listener ->
                        Arrays.stream(MethodUtils.getMethodsWithAnnotation(listener.getClass(), Handler.class))
                                .map(method -> {
                                    Handler handler = MethodUtils.getAnnotation(method, Handler.class, false, false);
                                    String topic = handler.topic();
                                    handlerRegistry.register(topic, listener, method);
                                    return topic;
                                }))
                .map(Topic::new)
                .collect(Collectors.toSet());

        platformClient.register(pubTopics, subTopics);

        eventPublisher.publishEvent(new ClientRegisterEvent());
    }
}
