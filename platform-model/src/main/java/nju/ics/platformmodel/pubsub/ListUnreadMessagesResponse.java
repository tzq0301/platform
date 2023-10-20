package nju.ics.platformmodel.pubsub;

import java.util.List;

public record ListUnreadMessagesResponse(List<MessageDTO> messages) {
}
