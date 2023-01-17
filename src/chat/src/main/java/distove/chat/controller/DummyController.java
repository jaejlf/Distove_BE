package distove.chat.controller;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Connection;
import distove.chat.enumerate.MessageType;
import distove.chat.repository.ConnectionRepository;
import distove.chat.service.MessageService;
import distove.chat.service.StorageService;
import distove.common.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 마이크로서비스 통신 전, 더미 데이터 생성을 위한 임시 컨트롤러 !
 */
@RequiredArgsConstructor
@RestController
public class DummyController {

    private final MessageService messageService;
    private final StorageService storageService;
    private final ConnectionRepository connectionRepository;

    @PostMapping("/pub/{channelId}")
    public void publishMessage(@RequestHeader("userId") Long userId,
                               @PathVariable Long channelId,
                               @RequestBody MessageRequest request) {
        messageService.publishMessage(userId, channelId, request);
    }

    @PostMapping("/new/connection/{channelId}")
    public void createConnection(@RequestHeader("userId") Long userId,
                                 @PathVariable Long channelId) {
        List<Long> connectedMemberIds = new ArrayList<>();
        connectedMemberIds.add(userId);
        connectionRepository.save(new Connection(channelId, connectedMemberIds));
    }

    @PostMapping("/one/{type}")
    public ResponseEntity<Object> uploadImgOne(@RequestPart("file") MultipartFile file,
                                               @PathVariable MessageType type) {
        return ResultResponse.success(HttpStatus.OK, "이미지 업로드 테스트", storageService.uploadToS3(file, type));
    }

}