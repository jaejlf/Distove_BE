package distove.chat.service;

import distove.chat.entity.EventFail;
import distove.chat.event.DelChannelEvent;
import distove.chat.event.DelChannelListEvent;
import distove.chat.exception.DistoveException;
import distove.chat.repository.EventFailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static distove.chat.enumerate.EventTopic.getEventQ;
import static distove.chat.exception.ErrorCode.EVENT_HANDLE_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final ConnectionService connectionService;
    private final MessageService messageService;
    private final EventFailRepository eventFailRepository;

    public void requestDelChannel(Long channelId) {
        getEventQ(DelChannelEvent.class)
                .add(new DelChannelEvent(channelId));
    }

    public void requestDelChannelList(List<Long> channelIds) {
        getEventQ(DelChannelListEvent.class)
                .add(new DelChannelListEvent(channelIds));
    }

    public void runDelChannel(DelChannelEvent event) {
        log.info(">>>>> CONSUME 'DEL CHANNEL' TOPIC");
        try {
            connectionService.clear(event.getChannelId());
            messageService.clear(event.getChannelId());
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

    public void runDelChannelList(DelChannelListEvent event) {
        log.info(">>>>> CONSUME 'DEL LIST CHANNEL' TOPIC");
        try {
            connectionService.clearAll(event.getChannelIds());
            messageService.clearAll(event.getChannelIds());
        } catch (Exception e) {
            eventFailRepository.save(new EventFail(event));
            throw new DistoveException(EVENT_HANDLE_ERROR);
        }
    }

}
