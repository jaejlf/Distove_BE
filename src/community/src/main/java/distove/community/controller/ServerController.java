package distove.community.controller;

import distove.common.ResultResponse;
import distove.community.dto.response.ServerDto;
import distove.community.entity.Member;
import distove.community.entity.Server;
import distove.community.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;


//    @GetMapping("/server")
//    public ResponseEntity<Object> getServersByUserId(@RequestHeader("userId") Long userId){
//        List<Member> servers = serverService.getServersByUserId(userId);
//        return ResultResponse.success(HttpStatus.OK,"전체 그룹의 채널 및 카테고리 조회",servers);
//    }

    @GetMapping("/server")
    public ResponseEntity<Object> getServersByUserId(@RequestHeader("userId") Long userId){
        List<ServerDto> servers = serverService.getServersByUserId(userId);
        return ResultResponse.success(HttpStatus.OK,"전체 그룹의 채널 및 카테고리 조회",servers);
    }
}
