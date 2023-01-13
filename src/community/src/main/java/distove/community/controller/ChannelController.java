package distove.community.controller;


import distove.community.entity.Channel;
import distove.community.entity.ChannelRequest;
import distove.community.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/channel")
    public Channel postNewChannel(@RequestHeader("userId") Long userId,
                                  @RequestBody ChannelRequest channelRequest){
        channelService.postNewChannel(channelRequest);
        return null;
    }

}
