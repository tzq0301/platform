package nju.ics.platformclientspringbootstarter.client;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import nju.ics.platformclientspringbootstarter.autoconfiguration.PlatformClientProperties;
import nju.ics.platformclientspringbootstarter.model.Topic;
import nju.ics.platformmodel.pubsub.ListUnreadMessagesRequest;
import nju.ics.platformmodel.pubsub.ListUnreadMessagesResponse;
import nju.ics.platformmodel.pubsub.MessageDTO;
import nju.ics.platformmodel.pubsub.PublishRequest;
import nju.ics.platformmodel.resource.KeepAliveRequest;
import nju.ics.platformmodel.resource.RegisterRequest;
import nju.ics.platformmodel.resource.RegisterResponse;
import nju.ics.platformmodel.resource.UnregisterRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PlatformClient {
    private final RestTemplate restTemplate;

    private final PlatformClientProperties properties;

    private String clientId;

    public PlatformClient(@Nonnull RestTemplate restTemplate,
                          @Nonnull PlatformClientProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public void register(@Nonnull Set<Topic> pubTopics, @Nonnull Set<Topic> subTopics) {
        var response = register(new RegisterRequest(properties.getClientType(), properties.getClientName(),
                pubTopics.stream().map(Topic::value).collect(Collectors.toSet()),
                subTopics.stream().map(Topic::value).collect(Collectors.toSet())));
        this.clientId = response.clientId();
    }

    public void unregister() {
        unregister(new UnregisterRequest(clientId));
    }

    public void keepAlive() {
        keepAlive(new KeepAliveRequest(clientId));
    }

    public <T> void publish(@Nonnull Topic topic, @Nullable T data) {
        publish(new PublishRequest(new MessageDTO(UUID.randomUUID().toString(), clientId, topic.value(), data, LocalDateTime.now())));
    }

    public List<MessageDTO> listUnreadMessagesResponse() {
        return listUnreadMessagesResponse(new ListUnreadMessagesRequest(clientId)).messages();
    }

    private RegisterResponse register(@Nonnull RegisterRequest request) {
        String url = String.format("%s/platform/resource/register", properties.getServerUrl());
        return restTemplate.postForObject(url, request, RegisterResponse.class);
    }

    private void unregister(@Nonnull UnregisterRequest request) {
        String url = String.format("%s/platform/resource/unregister", properties.getServerUrl());
        restTemplate.postForObject(url, request, Void.class);
    }

    private void keepAlive(@Nonnull KeepAliveRequest request) {
        Objects.requireNonNull(request);

        String url = String.format("%s/platform/resource/keepAlive", properties.getServerUrl());
        restTemplate.postForObject(url, request, Void.class);
    }

    private void publish(@Nonnull PublishRequest request) {
        String url = String.format("%s/platform/pubsub/publish", properties.getServerUrl());
        restTemplate.postForObject(url, request, Void.class);
    }

    private ListUnreadMessagesResponse listUnreadMessagesResponse(@Nonnull ListUnreadMessagesRequest request) {
        String url = String.format("%s/platform/pubsub/listUnreadMessages", properties.getServerUrl());
        return restTemplate.postForObject(url, request, ListUnreadMessagesResponse.class);
    }
}
