package distove.auth.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import distove.auth.exception.DistoveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static distove.auth.exception.ErrorCode.ACCOUNT_NOT_FOUND;
import static distove.auth.exception.ErrorCode.FILE_UPLOAD_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final AmazonS3Client amazonS3client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile multipartFile) {
        log.info("hello{}", multipartFile.getOriginalFilename());
        String fileName = multipartFile.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());

        try {
            InputStream inputStream = multipartFile.getInputStream();
            objectMetadata.setContentLength(inputStream.available());
            amazonS3client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new DistoveException(FILE_UPLOAD_ERROR);
        }
        return amazonS3client.getUrl(bucket, fileName).toString();
    }

    private void validateFileExists(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DistoveException(ACCOUNT_NOT_FOUND);
        }
    }
}
