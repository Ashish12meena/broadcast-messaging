package com.aigreentick.services.whatsapp.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.aigreentick.services.whatsapp.dto.template.FacebookApiResponse;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WhatsAppTemplateServiceImpl {
    private final WebClient webClient;

    public WhatsAppTemplateServiceImpl(
    @Value("${whatsapp.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Sends a WhatsApp template to Facebook Cloud API.
     *
     * @param bodyJson    JSON string payload
     * @param wabaId      WhatsApp Business Account ID
     * @param accessToken Facebook access token
     * @param apiVersion  Graph API version (e.g., v20.0)
     * @return FacebookApiResponse with data or error
     */

    public FacebookApiResponse<JsonNode> sendTemplateToFacebook(
            String bodyJson, String wabaId, String accessToken, String apiVersion) {
        try {
            JsonNode response = webClient.post()
                    .uri("/{apiVersion}/{wabaId}/message_templates", apiVersion, wabaId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .bodyValue(bodyJson)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("Template sent successfully to Facebook. WABA_ID={} Response={}", wabaId, response);
            return FacebookApiResponse.success(response, 200);

        } catch (WebClientResponseException ex) {
            // Facebook API returned an error
            log.error("Facebook API error: Status={} Response={}", ex.getStatusCode().value(),
                    ex.getResponseBodyAsString());
            return FacebookApiResponse.error(ex.getResponseBodyAsString(), ex.getStatusCode().value());

        } catch (Exception ex) {
            // Generic error
            log.error("Error calling Facebook Cloud API: {}", ex.getMessage(), ex);
            return FacebookApiResponse.error("Internal Server Error: " + ex.getMessage(), 500);
        }
    }
}
