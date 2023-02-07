package distove.chat.service;

import distove.chat.event.DelChannelEvent;
import distove.chat.event.DelChannelListEvent;
import distove.chat.event.NewChannelEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void requestDelChannelList(List<Long> channelIds) {
        getEventQ(DelChannelListEvent.class)
                .add(new DelChannelListEvent(channelIds));
    }

    public void runNewChannel(NewChannelEvent event) {
        log.info(">>>>> CONSUME 'NEW CHANNEL' TOPIC");
        connectionService.createConnection(event.getUserId(), event.getChannelId());
    }

    public void runDelChannel(DelChannelEvent event) {
        log.info(">>>>> CONSUME 'DEL CHANNEL' TOPIC");
        connectionService.clear(event.getChannelId());
        messageService.clear(event.getChannelId());
    }

    public void runDelChannelList(DelChannelListEvent event) {
        log.info(">>>>> CONSUME 'DEL LIST CHANNEL' TOPIC");
        connectionService.clearAll(event.getChannelIds());
        messageService.clearAll(event.getChannelIds());
    }

}
