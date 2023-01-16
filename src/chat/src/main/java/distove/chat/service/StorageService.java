package distove.chat.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import distove.chat.enumerate.MessageType;
import distove.chat.exception.DistoveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static distove.chat.exception.ErrorCode.FILE_EXTENTION_ERROR;
import static distove.chat.exception.ErrorCode.FILE_UPLOAD_ERROR;

@RequiredArgsConstructor
@Service
@Slf4j
public class StorageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadToS3(MultipartFile file, MessageType type) {

        // 확장자 체크
        String fileName = file.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf('.'));

        switch (type) {
            case IMAGE:
                if (!ext.equals(".jpg") && !ext.equals(".png") && !ext.equals(".PNG") && !ext.equals(".jpeg")) {
                    throw new DistoveException(FILE_EXTENTION_ERROR);
                }
                break;
            case VIDEO:
                if (!ext.equals(".mp4") && !ext.equals(".avi")) {
                    throw new DistoveException(FILE_EXTENTION_ERROR);
                }
                break;
        }

        // 업로드
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(bucket, "test", inputStream, objectMetadata);
        } catch (IOException e) {
            throw new DistoveException(FILE_UPLOAD_ERROR);
        }

        return amazonS3.getUrl(bucket, "test").toString();

    }

}

