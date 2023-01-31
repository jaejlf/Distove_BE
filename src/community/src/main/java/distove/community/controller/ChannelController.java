package distove.community.controller;

import distove.community.config.AuthorizedRole;
import distove.community.dto.request.ChannelCreateRequest;
import distove.community.dto.request.ChannelUpdateRequest;
import distove.community.dto.response.ChannelResponse;
import distove.community.dto.response.ResultResponse;
import distove.community.entity.Channel;
import distove.community.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static distove.community.config.AuthorizedRole.Auth.CAN_MANAGE_CHANNEL;
import static distove.community.dto.response.ChannelResponse.newChannelResponse;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @AuthorizedRole(name = CAN_MANAGE_CHANNEL)
    @PostMapping("/{serverId}/channel")
    public ResponseEntity<Object> createNewChannel(@RequestHeader("userId") Long userId,
                                                   @PathVariable Long serverId,
                                                   @RequestBody ChannelCreateRequest channelCreateRequest) {
        Channel newChannel = channelService.createNewChannel(userId, serverId, channelCreateRequest.getName(), channelCreateRequest.getCategoryId(), channelCreateRequest.getChannelTypeId());
        return ResultResponse.success(HttpStatus.OK, "채널 생성 성공",
                newChannelResponse(newChannel.getId(), newChannel.getName(), newChannel.getChannelTypeId()));
    }

    @AuthorizedRole(name = CAN_MANAGE_CHANNEL)
    @PatchMapping("/{serverId}")
    public ResponseEntity<Object> updateChannelName(@PathVariable Long serverId,
                                                    @RequestParam Long channelId,
                                                    @RequestBody ChannelUpdateRequest channelUpdateRequest) {
        ChannelResponse channel = channelService.updateChannelName(channelId, serverId, channelUpdateRequest);
        return ResultResponse.success(HttpStatus.OK, "채널 이름 수정 성공", channel);
    }

    @AuthorizedRole(name = CAN_MANAGE_CHANNEL)
    @DeleteMapping("/{serverId}")
    public ResponseEntity<Object> deleteChannelById(@PathVariable Long serverId,
                                                    @RequestParam Long channelId) {
        channelService.deleteChannelById(channelId, serverId);
        return ResultResponse.success(HttpStatus.OK, "채널 삭제 성공", null);
    }

}
