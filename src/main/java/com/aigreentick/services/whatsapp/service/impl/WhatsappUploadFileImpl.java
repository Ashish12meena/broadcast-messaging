package com.aigreentick.services.whatsapp.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.aigreentick.services.whatsapp.dto.upload.UploadMediaResponse;
import com.aigreentick.services.whatsapp.dto.upload.UploadOffsetResponse;
import com.aigreentick.services.whatsapp.dto.upload.UploadSessionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsappUploadFileImpl {
    private final WebClient webClient;

    @Value("${whatsapp.base-url}")
    private String baseUrl;

    /**
     * Step 1: Initiates an upload session with the Facebook Graph API.
     *
     * @param fileName    Name of the file to be uploaded (e.g., "invoice.pdf")
     * @param fileSize    Size of the file in bytes
     * @param mimeType    MIME type (e.g., "application/pdf", "image/jpeg")
     * @param wabaAppId   Your WABA-linked App ID
     * @param accessToken Access token with upload permission
     * @return Map containing the session ID (e.g., upload:123456)
     */

    public UploadSessionResponse initiateUploadSession(String fileName,
            long fileSize,
            String mimeType,
            String wabaAppId,
            String accessToken,
            String apiVersion) {

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment(apiVersion, wabaAppId, "uploads")
                .queryParam("file_name", fileName)
                .queryParam("file_length", fileSize)
                .queryParam("file_type", mimeType)
                .queryParam("access_token", accessToken)
                .build()
                .toUri();

        log.info("Initiating upload session: {}", uri);
        return webClient.post()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UploadSessionResponse.class)
                .doOnNext(response -> log.info("Upload session ID received: {}", response.getUploadSessionId()))
                .block();

    }

    /**
     * Step 2: Uploads the media file to the Facebook Graph API using the session
     * ID.
     *
     * @param sessionId   Upload session ID returned from initiateUploadSession
     *                    (e.g., "upload:123456")
     * @param file        File to upload
     * @param accessToken User access token with upload permissions
     * @return The media handle (h) that can be used in a WhatsApp template message
     * @throws IOException If file cannot be read
     */
    public UploadMediaResponse uploadMediaToFacebook(String sessionId,
            File file,
            String accessToken,
            String apiVersion,
            String offset) throws IOException {
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());
        }

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment(apiVersion, sessionId)
                .build()
                .toUri();

        log.info("Uploading media to: {}", uri);

        InputStreamResource fileResource = new InputStreamResource(new FileInputStream(file));

        UploadMediaResponse response = webClient.post()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "OAuth " + accessToken)
                .header("file_offset", offset)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(fileResource)
                .retrieve()
                .bodyToMono(UploadMediaResponse.class)
                .block();

        if (response == null || response.getFacebookImageUrl() == null) {
            throw new IllegalStateException("Upload failed or handle not returned");
        }

        log.info("Media handle received: {}", response.getFacebookImageUrl());
        return response;

    }

    /**
     * Gets the current file offset for an ongoing Facebook upload session.
     * Used to resume chunked uploads or verify completion.
     *
     * @param sessionId   The upload session ID (e.g., "upload:123456")
     * @param accessToken Valid user access token with upload permission
     * @param apiVersion  Graph API version (e.g., "v23.0")
     * @return UploadOffsetResponse with current file offset
     */
    public UploadOffsetResponse getUploadOffset(String sessionId,
            String accessToken,
            String apiVersion) {

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment(apiVersion, sessionId)
                .queryParam("access_token", accessToken)
                .build()
                .toUri();

        log.info("Checking upload offset: {}", uri);

        return webClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "OAuth " + accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UploadOffsetResponse.class)
                .doOnNext(resp -> log.info("Received file_offset: {}", resp.getFileOffset()))
                .block();
    }

}
