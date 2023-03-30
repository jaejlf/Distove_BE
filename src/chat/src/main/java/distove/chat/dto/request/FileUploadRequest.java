package distove.chat.dto.request;

import distove.chat.enumerate.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequest {
    private MultipartFile file;
    private MessageType type;
    private String parentId;
}
