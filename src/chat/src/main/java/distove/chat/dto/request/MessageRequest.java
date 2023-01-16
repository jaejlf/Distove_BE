package distove.chat.dto.request;

import distove.chat.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    private String id;
    private MultipartFile file;
    private MessageType type;
    private String content;
}
