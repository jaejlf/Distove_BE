package distove.chat.dto.request;

import distove.chat.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NotNull
    private Long userId;
    private MessageType type;
    private String id;
    private MultipartFile file;
    private String content;

}
