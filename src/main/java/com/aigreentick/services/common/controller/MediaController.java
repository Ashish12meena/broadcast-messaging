package com.aigreentick.services.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.MediaUploadRequestDto;
import com.aigreentick.services.common.dto.MediaUploadResponseDto;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.common.service.impl.MediaUploadServiceImpl;
import com.aigreentick.services.messaging.dto.ResponseStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/media")
public class MediaController {
    MediaUploadServiceImpl mediaUploadService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(@RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        MediaUploadResponseDto responseDto = mediaUploadService.uploadMedia(file, loginUser.getId());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Media Uploaded Successfully", responseDto));
    }

}
