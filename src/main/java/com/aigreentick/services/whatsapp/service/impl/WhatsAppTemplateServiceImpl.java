package com.aigreentick.services.whatsapp.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    public FacebookApiResponse<JsonNode> listTemplates(
            String wabaId, String accessToken, String apiVersion) {

        try {
            JsonNode response = webClient.get()
                    .uri("/{apiVersion}/{wabaId}/message_templates", apiVersion, wabaId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("Template list fetched. WABA_ID={} Response={}", wabaId, response);
            return FacebookApiResponse.success(response, 200);

        } catch (WebClientResponseException ex) {
            log.error("List templates failed: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return FacebookApiResponse.error(ex.getResponseBodyAsString(), ex.getStatusCode().value());
        } catch (Exception ex) {
            log.error("List templates unexpected error", ex);
            return FacebookApiResponse.error("Internal Server Error: " + ex.getMessage(), 500);
        }
    }

    public FacebookApiResponse<JsonNode> getTemplateDetails(
            String templateId, String accessToken, String apiVersion) {

        try {
            JsonNode response = webClient.get()
                    .uri("/{apiVersion}/{templateId}", apiVersion, templateId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("Template details fetched. TemplateID={} Response={}", templateId, response);
            return FacebookApiResponse.success(response, 200);

        } catch (WebClientResponseException ex) {
            log.error("Get template failed: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return FacebookApiResponse.error(ex.getResponseBodyAsString(), ex.getStatusCode().value());
        } catch (Exception ex) {
            log.error("Get template unexpected error", ex);
            return FacebookApiResponse.error("Internal Server Error: " + ex.getMessage(), 500);
        }
    }

    public FacebookApiResponse<JsonNode> deleteTemplate(
            String wabaId, String templateName, String accessToken, String apiVersion) {

        try {
            JsonNode response = webClient.method(HttpMethod.DELETE)
                    .uri("/{apiVersion}/{wabaId}/message_templates", apiVersion, wabaId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .bodyValue("{\"name\":\"" + templateName + "\"}")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            log.info("Template deleted. Name={} Response={}", templateName, response);
            return FacebookApiResponse.success(response, 200);

        } catch (WebClientResponseException ex) {
            log.error("Delete template failed: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            return FacebookApiResponse.error(ex.getResponseBodyAsString(), ex.getStatusCode().value());
        } catch (Exception ex) {
            log.error("Delete template unexpected error", ex);
            return FacebookApiResponse.error("Internal Server Error: " + ex.getMessage(), 500);
        }
    }

}
