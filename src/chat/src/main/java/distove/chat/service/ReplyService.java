package distove.chat.service;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.request.ReplyRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.dto.response.PagedMessageResponse;
import distove.chat.entity.Message;
import distove.chat.entity.Reply;
import distove.chat.entity.ReplyInfo;
import distove.chat.enumerate.MessageType;
import distove.chat.exception.DistoveException;
import distove.chat.repository.ConnectionRepository;
import distove.chat.repository.MessageRepository;
import distove.chat.repository.ReplyRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.entity.Reply.newReply;
import static distove.chat.entity.ReplyInfo.newReplyInfo;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;
import static distove.chat.exception.ErrorCode.REPLY_PARENT_NOT_FOUND_ERROR;

@Service
public class ReplyService extends PublishService {

    private final ReplyRepository replyRepository;

    public ReplyService(StorageService storageService, MessageRepository messageRepository, ConnectionRepository connectionRepository, UserClient userClient, ReplyRepository replyRepository) {
        super(storageService, messageRepository, connectionRepository, userClient);
        this.replyRepository = replyRepository;
    }


    @Override
    public MessageResponse publishMessage(Long userId, Long channelId, MessageRequest request) {
        checkChannelExist(channelId);
        Message parent = getParentMessage(request.getParentId());
        Message message = createMessageByType(channelId, request, userId);
        Reply reply = replyRepository.save(newReply(parent.getId(), message));

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(reply, writer, userId);
    }

    @Override
    public MessageResponse publishFile(Long userId, Long channelId, MessageType type, FileUploadRequest request) {
        checkChannelExist(channelId);
        Message parent = getParentMessage(request.getParentId());
        String fileUploadUrl = storageService.uploadToS3(request.getFile(), type);
        Message message = newMessage(channelId, userId, type, fileUploadUrl);
        Reply reply = replyRepository.save(newReply(parent.getId(), message));

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(reply, writer, userId);
    }

    public void createReply(Long userId, String parentId, ReplyRequest request) {
        Message parent = messageRepository.findById(parentId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        ReplyInfo replyInfo = newReplyInfo(request.getReplyName(), userId);
        parent.addReplyInfo(replyInfo);
        messageRepository.save(parent);
    }

    public List<MessageResponse> getParentByChannelId(Long userId, Long channelId) {
        List<Message> messages = messageRepository.findAllByChannelIdAndReplyInfoIsNotNull(channelId);
        return convertMessageToDtoWithReplyInfo(userId, messages);
    }

    public PagedMessageResponse getChildrenByParentId(Long userId, String parentId, int page) {
        Pageable pageable = PageRequest.of(page - 1, 5); // TODO : 테스트를 용이하게 하기 위해 임의로 5로 설정 (추후 30으로 변경 예정)
        Page<Reply> replyPage = replyRepository.findAllByParentId(parentId, pageable);

        int totalPage = replyPage.getTotalPages();
        List<MessageResponse> messageResponses = replyPage.getContent()
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getMessage().getUserId()), userId))
                .collect(Collectors.toList());

        return PagedMessageResponse.of(totalPage, messageResponses);
    }

    private Message getParentMessage(String parentId) {
        return messageRepository.findByIdAndReplyInfoIsNotNull(parentId)
                .orElseThrow(() -> new DistoveException(REPLY_PARENT_NOT_FOUND_ERROR));
    }

}
