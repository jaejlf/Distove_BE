package distove.chat.service;

import distove.chat.dto.request.FileUploadRequest;
import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.request.ReplyRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.entity.Message;
import distove.chat.entity.ReplyInfo;
import distove.chat.enumerate.MessageType;
import distove.chat.exception.DistoveException;
import distove.chat.repository.MessageRepository;
import distove.chat.repository.ReplyRepository;
import distove.chat.web.UserClient;
import distove.chat.web.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static distove.chat.entity.Message.newMessage;
import static distove.chat.entity.Reply.newReply;
import static distove.chat.entity.ReplyInfo.newReplyInfo;
import static distove.chat.exception.ErrorCode.MESSAGE_NOT_FOUND_ERROR;

@Service
public class ReplyService extends PublishService {

    private final ReplyRepository replyRepository;

    public ReplyService(StorageService storageService, MessageRepository messageRepository, UserClient userClient, ReplyRepository replyRepository) {
        super(storageService, messageRepository, userClient);
        this.replyRepository = replyRepository;
    }

    @Override
    public MessageResponse publishMessage(Long channelId, MessageRequest request) {
        Long userId = request.getUserId();

        Message message = createMessageByType(channelId, request, userId);
        replyRepository.save(newReply(request.getParentId(), message));

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    @Override
    public MessageResponse publishFile(Long channelId, MessageType type, FileUploadRequest request) {
        Long userId = request.getUserId();

        String fileUploadUrl = storageService.uploadToS3(request.getFile(), type);
        Message message = newMessage(channelId, userId, type, fileUploadUrl);
        replyRepository.save(newReply(request.getParentId(), message));

        UserResponse writer = userClient.getUser(userId);
        return MessageResponse.of(message, writer, userId);
    }

    public void createReply(Long userId, String parentId, ReplyRequest request) {
        Message parent = messageRepository.findById(parentId)
                .orElseThrow(() -> new DistoveException(MESSAGE_NOT_FOUND_ERROR));

        ReplyInfo replyInfo = newReplyInfo(request.getReplyName(), userId);
        parent.addReplyInfo(replyInfo);
        messageRepository.save(parent);
    }

    public List<MessageResponse> getRepliesByChannelId(Long userId, Long channelId) {
        return messageRepository.findAllByChannelIdAndReplyInfoIsNotNull(channelId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getUserId()), userId))
                .collect(Collectors.toList());
    }

    public List<MessageResponse> getChildrenByParentId(Long userId, String parentId) {
        return replyRepository.findAllByParentId(parentId)
                .stream()
                .map(x -> MessageResponse.of(x, userClient.getUser(x.getMessage().getUserId()), userId))
                .collect(Collectors.toList());
    }

}
