package distove.community.controller;

import distove.community.dto.response.CategoryResponse;
import distove.community.dto.response.ResultResponse;
import distove.community.entity.Member;
import distove.community.entity.Server;
import distove.community.service.CategoryService;
import distove.community.service.MemeberService;
import distove.community.service.ServerService;
import distove.community.web.UserClient;
import distove.community.web.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final MemeberService memeberService;
    private final CategoryService categoryService;
    private final UserClient userClient;

    @GetMapping("/server")
    public ResponseEntity<Object> getServersByUserId(@RequestHeader("userId") Long userId){
        List<Server> servers = serverService.getServersByUserId(userId);
        return ResultResponse.success(HttpStatus.OK,"서버 리스트 조회(By user)",servers);
    }

    @GetMapping("/server/{serverId}")
    public ResponseEntity<Object> getServersByUserId(@RequestHeader("userId") Long userId,
                                                     @PathVariable("serverId") Long serverId){
        List<CategoryResponse> categories = categoryService.getCategoriesWithChannelsByServerId(serverId);
        return ResultResponse.success(HttpStatus.OK,"그룹의 채널 및 카테고리 조회",categories);
    }

    @PostMapping("/server")
    public ResponseEntity<Object> createNewServer(@RequestHeader("userId") Long userId,
                                                  @RequestPart("name") String name,
                                                  @RequestPart(required = false, value = "image") MultipartFile image){
        Server server =  serverService.createNewServer(userId, name,image);
        return ResultResponse.success(HttpStatus.OK,"서버 생성 성공",server);
    }

    @PatchMapping("/server/{serverId}")
    public ResponseEntity<Object> updateServer(@RequestHeader("userId") Long userId,
                                               @PathVariable("serverId") Long serverId,
                                               @RequestPart(required = false, value = "imgUrl") String imgUrl,
                                               @RequestPart("name") String name,
                                               @RequestPart("image") MultipartFile image){
        Server server = serverService.updateServer(serverId,name,imgUrl,image);
        return ResultResponse.success(HttpStatus.OK,"서버 수정 성공",server);
    }
    @DeleteMapping("/server/{serverId}")
    public ResponseEntity<Object> deleteServerById(@RequestHeader("userId") Long userId,
                                               @PathVariable("serverId") Long serverId){
        serverService.deleteServerById(serverId);
        return ResultResponse.success(HttpStatus.OK,"서버 삭제 성공",null);
    }
    @GetMapping("/server/{serverId}/member/")
    public ResponseEntity<Object> getMembersByServerId(@RequestHeader("userId") Long userId,
                                                     @PathVariable("serverId") Long serverId){
        List<UserResponse> users = new ArrayList<>();
        List<Member> members=memeberService.getMembersByServerId(serverId);
        for (Member member : members) {
            users.add(userClient.getUser(member.getUserId()));
        }
        return ResultResponse.success(HttpStatus.OK,"서버 내 멤버 리스트 조회",users);
    }
}
