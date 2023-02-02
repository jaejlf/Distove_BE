package distove.auth.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class UpdateProfileImageRequest {
    private MultipartFile profileImg;
}
