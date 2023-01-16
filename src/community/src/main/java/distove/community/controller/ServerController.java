package distove.community.controller;

import distove.common.ResultResponse;
import distove.community.dto.request.ServerRequest;
import distove.community.dto.response.ServerResponse;
import distove.community.entity.Server;
import distove.community.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;


//    @GetMapping("/server")
//    public ResponseEntity<Object> getServersByUserId(@RequestHeader("userId") Long userId){
//        List<Member> servers = serverService.getServersByUserId(userId);
//        return ResultResponse.success(HttpStatus.OK,"전체 그룹의 채널 및 카테고리 조회",servers);
//    }

    @PostMapping("/server")
    public Server createNewServer(@RequestHeader("userId") Long userId,
                                  @RequestBody ServerRequest serverRequest){
        return serverService.createNewServer(serverRequest);
    }

    @GetMapping("/server")
    public ResponseEntity<Object> getServerById(@RequestHeader("serverId") Long serverId){
        ServerResponse serverResponse = serverService.getServerById(serverId);
        return ResultResponse.success(HttpStatus.OK,"그룹의 채널 및 카테고리 조회",serverResponse);
    }
}
