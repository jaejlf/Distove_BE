package distove.chat.service;

import distove.chat.event.DelChannelEvent;
import distove.chat.event.NewChannelEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static distove.chat.enumerate.EventTopic.getEventQ;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final ConnectionService connectionService;
    private final MessageService messageService;

    public void requestNewChannel(Long userId, Long channelId) {
        getEventQ(NewChannelEvent.class)
                .add(NewChannelEvent.of(userId, channelId));
    }

    public void requestDelChannel(Long channelId) {
        getEventQ(DelChannelEvent.class)
                .add(new DelChannelEvent(channelId));
    }

    public void runNewChannel(NewChannelEvent event) {
        log.info(">>>>> CONSUME 'NEW CHANNEL' TOPIC");
        connectionService.createConnection(event.getUserId(), event.getChannelId());
    }

    public void runDelChannel(DelChannelEvent event) {
        log.info(">>>>> CONSUME 'DEL CHANNEL' TOPIC");
        connectionService.clearAll(event.getChannelId());
        messageService.clearAll(event.getChannelId());
    }

}
