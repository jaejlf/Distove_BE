package distove.chat.controller;

import distove.chat.event.DeleteChannelEvent;
import distove.chat.event.DeleteChannelsEvent;
import distove.chat.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static distove.chat.event.process.EventTopic.getEventQ;

@RestController
@RequestMapping("/web")
@RequiredArgsConstructor
public class WebController {

    private final ConnectionService connectionService;

    /**
     * 새로운 Connection 생성
     *
     * @when community 서비스 -> 채널 생성 시
     */
    @PostMapping("/connection/server/{serverId}")
    public void createConnection(@PathVariable Long serverId,
                                 @RequestParam Long channelId) {
        connectionService.createConnection(serverId, channelId);
    }

    /**
     * 채널 삭제
     *
     * @when community 서비스 -> 채널 삭제 시
     */
    @DeleteMapping("/clear/channel/{channelId}")
    public void deleteChannel(@PathVariable Long channelId) {
        getEventQ(DeleteChannelEvent.class).add(new DeleteChannelEvent(channelId));
    }

    /**
     * 채널 리스트 삭제
     *
     * @when community 서비스 -> 서버 삭제 시
     */
    @DeleteMapping("/clear/channels")
    public void deleteChannels(@RequestParam List<Long> channelIds) {
        getEventQ(DeleteChannelsEvent.class).add(new DeleteChannelsEvent(channelIds));
    }

}
