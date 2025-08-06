package com.aigreentick.services.common.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.common.dto.MediaUploadResponseDto;
import com.aigreentick.services.common.mapper.MediaMapper;
import com.aigreentick.services.common.model.Media;
import com.aigreentick.services.common.repository.MediaRepository;
import com.aigreentick.services.common.util.FileMetaData;
import com.aigreentick.services.template.exception.MediaUploadException;
import com.aigreentick.services.whatsapp.dto.upload.UploadMediaResponse;
import com.aigreentick.services.whatsapp.dto.upload.UploadSessionResponse;
import com.aigreentick.services.whatsapp.service.impl.WhatsappUploadFileImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaUploadServiceImpl {

    private final WhatsappUploadFileImpl whatsappUploadFile;
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final UserService userService;

     @Value("${apiVersion}")
    private String apiVersion;

    public MediaUploadResponseDto uploadMedia(MultipartFile file,
            Long userId) {

               User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found in Upload Media"));
            //    String wabaAppId = user.getWhatsappCredential().getWabaId();
            //    String accessToken = user.getWhatsappCredential().getAccessToken();
               String wabaAppId ="" ;
               String accessToken ="" ;
        String offset = "0";
        try {
            FileMetaData fileMeta = extractFileDetails(file);
            File fullFile = convertMultipartToFile(file);

            UploadSessionResponse sessionResponse = whatsappUploadFile.initiateUploadSession(
                    fileMeta.getFileName(), fileMeta.getFileSize(), fileMeta.getMimeType(),
                    wabaAppId, accessToken, apiVersion);

            String sessionId = sessionResponse.getUploadSessionId();

            // Step 3: Upload file normally (retry if needed)
            UploadMediaResponse uploadMediaResponse = tryUploadToFacebook(sessionId, fullFile, accessToken, apiVersion,
                    offset);

            Media media = saveMediaRecord(userId, sessionId, fileMeta, uploadMediaResponse.getFacebookImageUrl());
            return mediaMapper.toDto(media);

        } catch (IOException ex) {
            throw new MediaUploadException("Failed to upload file", ex);
        }
    }

    // --- Modular Helpers ---

    private UploadMediaResponse tryUploadToFacebook(String sessionId, File file, String accessToken, String apiVersion,
            String offset) {
        try {
            return whatsappUploadFile.uploadMediaToFacebook(sessionId, file, accessToken, apiVersion, offset);
        } catch (Exception e) {
            log.warn("Initial upload failed, attempting to resume with offset...", e);
            try {
                String newOffset = whatsappUploadFile
                        .getUploadOffset(sessionId, accessToken, apiVersion)
                        .getFileOffset();

                return whatsappUploadFile.uploadMediaToFacebook(sessionId, file, accessToken, apiVersion, newOffset);

            } catch (IOException retryEx) {
                throw new MediaUploadException("Retry upload failed", retryEx);
            }
        }
    }

    private FileMetaData extractFileDetails(MultipartFile file) {
        return new FileMetaData(
                Objects.requireNonNull(file.getOriginalFilename()),
                file.getSize(),
                file.getContentType());
    }

    private Media saveMediaRecord(Long userId, String sessionId, FileMetaData meta, String handle) {
        Media media = mediaMapper.toEntity(userId, sessionId, meta, handle);
        return mediaRepository.save(media);
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("upload_", Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(convFile);
        return convFile;
    }

}
