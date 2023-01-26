package distove.chat.service;

import distove.chat.dto.request.ReplyRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.entity.Message;
import distove.chat.entity.ReplyInfo;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.web.UserClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static distove.chat.entity.ReplyInfo.newReplyInfo;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;

@Service
public class ReplyService extends PublishService {

    public ReplyService(StorageService storageService, MessageRepository messageRepository, ConnectionRepository connectionRepository, UserClient userClient) {
        super(storageService, messageRepository, connectionRepository, userClient);
    }

    public void createReply(Long userId, String parentId, ReplyRequest request) {
        Message parent = messageRepository.findById(parentId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        ReplyInfo replyInfo = newReplyInfo(request.getReplyName(), userId);
        parent.addReplyInfo(replyInfo);
        messageRepository.save(parent);
    }

    public List<MessageResponse> getParentByChannelId(Long userId, Long channelId) {
        return messageRepository.findAllByChannelIdAndReplyInfoIsNotNull(channelId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());
    }

    public PagedMessageResponse getChildrenByParentId(Long userId, String parentId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 5); // TODO : 테스트를 용이하게 하기 위해 임의로 5로 설정 (추후 30으로 변경 예정)
        Page<Message> replyPage = messageRepository.findAllByParentId(parentId, pageable);

        int totalPage = replyPage.getTotalPages();
        List<MessageResponse> messageResponses = replyPage.getContent()
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());

        return PagedMessageResponse.of(totalPage, messageResponses);
    }

}
