package distove.community.controller;


import distove.common.ResultResponse;
import distove.community.dto.request.ChannelRequest;
import distove.community.dto.response.ChannelDto;
import distove.community.entity.Channel;
import distove.community.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    @PostMapping("/channel")
    public ResponseEntity<Object> createNewChannel(@RequestHeader("userId") Long userId,
                                                   @RequestBody ChannelRequest channelRequest){
        Channel newChannel = channelService.createNewChannel(channelRequest);

        return ResultResponse.success(HttpStatus.OK,"채널 생성 성공",
                new ChannelDto(newChannel.getId(),newChannel.getName(),newChannel.getChannelTypeId()));
    }

//    @PatchMapping("/channel")
//    public ResponseEntity<Object> updateChannelName(@RequestHeader("userId") Long userId,
//                                                   @RequestBody ChannelUpdateRequest channelUpdateRequest){
//        Channel channel = channelService.updateChannelName(channelUpdateRequest);
//
//        return ResultResponse.success(HttpStatus.OK,"채널 생성 성공",channel)
//    }
    @DeleteMapping("/channel")
    public ResponseEntity<Object> deleteChannelById(@RequestHeader("channelId") Long channelId){
        channelService.deleteChannelById(channelId);
        return ResultResponse.success(HttpStatus.OK,"채널 생성 성공",null);
    }
}
