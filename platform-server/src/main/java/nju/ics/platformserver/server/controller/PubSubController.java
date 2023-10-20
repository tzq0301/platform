package nju.ics.platformserver.server.controller;

import jakarta.annotation.Nonnull;
import nju.ics.platformmodel.pubsub.ListUnreadMessagesRequest;
import nju.ics.platformmodel.pubsub.ListUnreadMessagesResponse;
import nju.ics.platformmodel.pubsub.MessageDTO;
import nju.ics.platformmodel.pubsub.PublishRequest;
import nju.ics.platformserver.pubsub.Topic;
import nju.ics.platformserver.server.service.PubSubService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pubsub")
public class PubSubController {
    private final PubSubService pubsubService;

    public PubSubController(@Nonnull PubSubService pubsubService) {
        this.pubsubService = pubsubService;
    }

    @PostMapping("/publish")
    public void publish(@Nonnull @RequestBody PublishRequest request) {
        MessageDTO message = request.message();
        this.pubsubService.publish(new nju.ics.platformserver.pubsub.Message<>(message.id(), message.publisherId(), new Topic(message.topic()), message.data(), message.createTime()));
    }

    @PostMapping("/listUnreadMessages")
    public ListUnreadMessagesResponse listUnreadMessages(@Nonnull @RequestBody ListUnreadMessagesRequest request) {
        List<MessageDTO> messages = pubsubService.listUnreadMessages(request.clientId())
                .stream()
                .map(message -> new MessageDTO(message.id(), message.publisherId(),
                        message.topic().value(), message.data(), message.createTime()))
                .toList();
        return new ListUnreadMessagesResponse(messages);
    }
}
