package nju.ics.platformclientspringbootstarter.publish;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import nju.ics.platformclientspringbootstarter.client.PlatformClient;
import nju.ics.platformclientspringbootstarter.model.Topic;
import org.springframework.stereotype.Component;

@Component
public class Publisher {
    private final PlatformClient platformClient;

    public Publisher(@Nonnull PlatformClient platformClient) {
        this.platformClient = platformClient;
    }

    public <T> void publish(@Nonnull Topic topic, @Nullable T data) {
        platformClient.publish(topic, data);
    }
}
