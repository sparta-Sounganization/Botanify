package com.sounganization.botanify.domain.s3.controller;

import com.sounganization.botanify.domain.s3.dto.req.ImageUploadReqDto;
import com.sounganization.botanify.domain.s3.dto.res.ImageUrlResDto;
import com.sounganization.botanify.domain.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Service s3service;

    public enum ImageDomainPath {
        users, posts, plants, diaries
    }

    @PostMapping("/{domain}/images")
    public ResponseEntity<ImageUrlResDto> uploadImage(@PathVariable ImageDomainPath domain, @RequestBody ImageUploadReqDto reqDto) {
        ImageUrlResDto resDto = s3service.getPreSignedUrl(String.valueOf(domain), reqDto);
        return ResponseEntity.ok(resDto);
    }

    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImage(@RequestBody ImageUploadReqDto reqDto) {
        s3service.deleteImage(reqDto.fileName());
        return ResponseEntity.noContent().build();
    }

}
