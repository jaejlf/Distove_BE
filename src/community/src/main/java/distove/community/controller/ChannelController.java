package distove.community.controller;


import distove.common.ResultResponse;
import distove.community.entity.Channel;
import distove.community.dto.request.ChannelRequest;
import distove.community.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.List;

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
