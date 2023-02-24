package distove.community.controller;

import distove.community.config.AuthorizedRole;
import distove.community.config.RequestUser;
import distove.community.dto.response.CategoryResponse;
import distove.community.dto.response.InvitationResponse;
import distove.community.dto.response.ResultResponse;
import distove.community.entity.Server;
import distove.community.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static distove.community.config.AuthorizedRole.Auth.CAN_DELETE_SERVER;
import static distove.community.config.AuthorizedRole.Auth.CAN_MANAGE_SERVER;

@RestController
@RequiredArgsConstructor
public class ServerController {

    @Value("${invitation.expire.days}")
    private Long days;

    @Value("${invitation.available.count}")
    private int count;
    private final ServerService serverService;

    @GetMapping("/server")
    public ResponseEntity<Object> getServersByUserId(@RequestUser Long userId) {
        List<Server> servers = serverService.getServersByUserId(userId);
        return ResultResponse.success(HttpStatus.OK, "서버 리스트 조회(By user)", servers);
    }


    @GetMapping("/server/{serverId}")
    public ResponseEntity<Object> getCategoriesWithChannelsByServerId(@PathVariable("serverId") Long serverId) {
        List<CategoryResponse> categories = serverService.getCategoriesWithChannelsByServerId(serverId);
        return ResultResponse.success(HttpStatus.OK, "그룹의 채널 및 카테고리 조회", categories);
    }

    @PostMapping("/server")
    public ResponseEntity<Object> createNewServer(@RequestUser Long userId,
                                                  @RequestPart("name") String name,
                                                  @RequestPart(required = false, value = "image") MultipartFile image) {
        Server server = serverService.createNewServer(userId, name, image);
        return ResultResponse.success(HttpStatus.OK, "서버 생성 성공", server);
    }

    @AuthorizedRole(name = CAN_MANAGE_SERVER)
    @PatchMapping("/server/{serverId}")
    public ResponseEntity<Object> updateServer(@PathVariable("serverId") Long serverId,
                                               @RequestPart(required = false, value = "imgUrl") String imgUrl,
                                               @RequestPart("name") String name,
                                               @RequestPart(required = false, value = "image") MultipartFile image) {
        Server server = serverService.updateServer(serverId, name, imgUrl, image);
        return ResultResponse.success(HttpStatus.OK, "서버 수정 성공", server);
    }

    @AuthorizedRole(name = CAN_DELETE_SERVER)
    @DeleteMapping("/server/{serverId}")
    public ResponseEntity<Object> deleteServerById(@PathVariable("serverId") Long serverId) {
        serverService.deleteServerById(serverId);
        return ResultResponse.success(HttpStatus.OK, "서버 삭제 성공", null);
    }

    @PostMapping("/server/invitation/{serverId}")
    public ResponseEntity<Object> createInvitation(@RequestUser Long userId,
                                                   @PathVariable ("serverId") Long serverId){
        String inviteCode = serverService.createInvitation(userId, serverId, days, count);
        return ResultResponse.success(HttpStatus.OK, "초대 코드 생성 성공", inviteCode);
    }

    @GetMapping("/server/invitations/{serverId}")
    public ResponseEntity<Object> listInvitations(@RequestUser Long userId,
                                                  @PathVariable ("serverId") Long serverId) {
        List<InvitationResponse> invitations = serverService.getInvitations(userId, serverId);
        return ResultResponse.success(HttpStatus.OK, "초대 리스트 조회 성공", invitations);
    }

    @DeleteMapping("/server/invitation/{inviteCode}")
    public ResponseEntity<Object> deleteInvitation(@RequestUser Long userId,
                                                   @PathVariable ("inviteCode") String inviteCode) {
        serverService.deleteInvitation(userId, inviteCode);
        return ResultResponse.success(HttpStatus.OK, "초대 코드 삭제 성공", inviteCode);

    }
}
