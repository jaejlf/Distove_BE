package distove.chat.service;

import distove.chat.dto.request.ReplyRequest;
import distove.chat.entity.Message;
import distove.chat.entity.ReplyInfo;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static distove.chat.entity.ReplyInfo.*;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReplyService {

    private final MessageRepository messageRepository;
    private final ReplyRepository replyRepository;


    public void createReply(Long userId, String parentId, ReplyRequest request) {
        Message parent = messageRepository.findById(parentId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        ReplyInfo replyInfo = newReplyInfo(request.getReplyName(), userId);
        parent.addReplyInfo(replyInfo);
        messageRepository.save(parent);
    }

}
