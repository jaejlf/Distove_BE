package distove.chat.service;

import distove.chat.dto.request.ReplyRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.entity.Message;
import distove.chat.entity.ReplyInfo;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.repository.ReplyRepository;
import distove.chat.web.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static distove.chat.entity.ReplyInfo.newReplyInfo;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReplyService {

    private final MessageRepository messageRepository;
    private final ReplyRepository replyRepository;
    private final UserClient userClient;

    public void createReply(Long userId, String parentId, ReplyRequest request) {
        Message parent = messageRepository.findById(parentId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        ReplyInfo replyInfo = newReplyInfo(request.getReplyName(), userId);
        parent.addReplyInfo(replyInfo);
        messageRepository.save(parent);
    }

    public List<MessageResponse> getChildrenByParentId(Long userId, String parentId) {
        return replyRepository.findAllByParentId(parentId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getMessage().getUserId()), userId))
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getRepliesByChannelId(Long userId, Long channelId) {
        return messageRepository.findAllByChannelIdAndReplyInfoIsNotNull(channelId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());
    }

}
