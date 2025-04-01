package com.example.dorandroan.controller;

import com.example.dorandroan.dto.ImageRequestDto;
import com.example.dorandroan.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @PostMapping()
    public ResponseEntity<Map<String, String>> upload(@Valid @RequestBody ImageRequestDto requestDto) {
        return ResponseEntity.ok(Map.of("uploadUrl", s3Service.generatePresignedUrl(requestDto)));
    }
}
