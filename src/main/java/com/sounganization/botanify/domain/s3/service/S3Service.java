package com.sounganization.botanify.domain.s3.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.s3.dto.req.ImageUploadReqDto;
import com.sounganization.botanify.domain.s3.dto.res.ImageUrlResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}") private String bucket;
    @Value("${aws.s3.endpoint}") private String endpoint;

    private static final Long SIGN_LIFE_TIME = 1000*60L;    // 1분
    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg",".jpeg",".png",".webp");

    // 전달된 접두사와 파일명으로 key 를 생성하는 pre-signing 기본 로직 메서드
    public ImageUrlResDto getPreSignedUrl(String prefix, ImageUploadReqDto reqDto) {

        // 파일 확장자 검사
        this.checkExtensionAllowed(reqDto.fileName());

        // prefix:UUID:fileName 규칙의 키를 생성한 후, URL 에 적합하도록 인코딩
        String rawKey = String.format("%s:%s:%s", prefix, UUID.randomUUID(), reqDto.fileName());
        String encodedKey = URLEncoder.encode(rawKey, StandardCharsets.UTF_8);

        // 업로드를 위한 PreSigned URL 생성
        String preSignedUrl = generatePreSignedUrl(encodedKey);

        // 업로드 URL & 공개 조회용 URL 반환
        return new ImageUrlResDto(preSignedUrl, String.format("%s/%s/%s", endpoint, bucket, encodedKey));
    }

    // 리스트 요청 처리용 번들 메서드
    public List<ImageUrlResDto> getPreSignedUrls(String prefix, List<ImageUploadReqDto> reqDtos) {
        return reqDtos.stream().map(reqDto -> this.getPreSignedUrl(prefix, reqDto)).toList();
    }

    public void deleteImage(String fileName) {
        if(!fileName.startsWith(endpoint)){
            throw new CustomException(ExceptionStatus.BAD_REQUEST);
        }

        String key = fileName.substring(endpoint.length() + 1);

        try {
            s3Client.deleteObject(b -> b.bucket(bucket).key(key));
        }
        catch (S3Exception ex) {
            throw new CustomException(ExceptionStatus.BAD_REQUEST);
        }
    }

    private String generatePreSignedUrl(String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ_WRITE)
                .build();

        PresignedPutObjectRequest preSignedRequest = s3Presigner.presignPutObject(p -> p
                        .signatureDuration(Duration.ofMillis(SIGN_LIFE_TIME))
                        .putObjectRequest(putObjectRequest));

        return preSignedRequest.url().toString();
    }

    // 서비스에서 허용하는 확장자인지 검사하는 유틸 메서드
    private void checkExtensionAllowed(String fileName) {
        // 전달된 실제 파일명의 확장자를 분리하며 소문자로 변환한 값
        String extension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();

        // 확장자 값이 정적 패턴 목록에 존재하지 않으면 예외 반환
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CustomException(ExceptionStatus.BAD_REQUEST);
        }
    }

}
