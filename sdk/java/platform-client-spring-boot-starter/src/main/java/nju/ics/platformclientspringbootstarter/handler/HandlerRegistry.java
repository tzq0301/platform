package nju.ics.platformclientspringbootstarter.handler;

import jakarta.annotation.Nonnull;
import nju.ics.platformclientspringbootstarter.model.Topic;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HandlerRegistry {
    private final Map<Topic, HandlerInstance> handlerMap;

    public HandlerRegistry() {
        this.handlerMap = new ConcurrentHashMap<>();
    }

    public void register(@Nonnull final String topic, @Nonnull final Object obj, @Nonnull final Method method) {
        handlerMap.put(new Topic(topic), new HandlerInstance(obj, method));
    }

    public HandlerInstance getMethod(@Nonnull final Topic topic) {
        Objects.requireNonNull(topic);

        return handlerMap.get(topic);
    }
}
