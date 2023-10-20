package nju.ics.platformserver.pubsub;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Lombok;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ToString(exclude = {"pubsub", "messages", "offset"})
public class Client implements Publisher, Subscriber {
    @Getter
    private final String id;

    @Getter
    private final String name;

    @Getter
    private final String type; // 主要用于前端画图

    @Getter
    private final LocalDateTime createTime;

    private final PubSub pubsub;

    private final List<Message<?>> messages;

    private int offset;

    public Client(@Nonnull PubSub pubsub, @Nonnull String name, @Nonnull String type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.createTime = LocalDateTime.now();
        this.pubsub = pubsub;
        this.messages = new ArrayList<>();
        this.offset = 0;

        Clients.add(this); // 取巧的做法（不推荐，以后有机会再改）
    }

    @Override
    public <T> void publish(@Nonnull Topic topic, @Nullable T data) {
        this.publish(new Message<>(id, topic, data));
    }

    public <T> void publish(@Nonnull Message<T> message) {
        pubsub.publish(message);
    }

    @Override
    public <T> void onMessage(@Nonnull Message<T> message) {
        messages.add(message);
    }

    public List<Message<?>> listUnreadMessages() {
        int size = this.messages.size();
        List<Message<?>> unreadMessages = this.messages.subList(this.offset, size);
        this.offset = size;
        return unreadMessages;
    }
}
