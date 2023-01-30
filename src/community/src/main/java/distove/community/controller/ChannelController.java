package distove.community.controller;


import distove.community.dto.request.ChannelRequest;
import distove.community.dto.request.ChannelUpdateRequest;
import distove.community.dto.response.ChannelDto;
import distove.community.dto.response.ChannelUpdateResponse;
import distove.community.dto.response.ResultResponse;
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
        Channel newChannel = channelService.createNewChannel(userId, channelRequest.getName(),channelRequest.getCategoryId(),channelRequest.getChannelTypeId());

        return ResultResponse.success(HttpStatus.OK,"채널 생성 성공",
                new ChannelDto(newChannel.getId(),newChannel.getName(),newChannel.getChannelTypeId()));
    }

    @PatchMapping("/channel/{channelId}")
    public ResponseEntity<Object> updateChannelName(@RequestHeader("userId") Long userId,
                                                    @PathVariable("channelId") Long channelId,
                                                    @RequestBody ChannelUpdateRequest channelUpdateRequest){
        ChannelUpdateResponse channelUpdateResponse = channelService.updateChannelName(channelId,channelUpdateRequest);

        return ResultResponse.success(HttpStatus.OK,"채널 이름 수정 성공",channelUpdateResponse);
    }
    @DeleteMapping("/channel/{channelId}")
    public ResponseEntity<Object> deleteChannelById(@RequestHeader("userId") Long userId,
                                                    @PathVariable("channelId") Long channelId){
        channelService.deleteChannelById(channelId);
        return ResultResponse.success(HttpStatus.OK,"채널 삭제 성공",null);
    }
}
