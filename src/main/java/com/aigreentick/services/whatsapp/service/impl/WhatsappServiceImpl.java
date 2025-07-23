package com.aigreentick.services.whatsapp.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.aigreentick.services.common.enums.MessageStatusEnum;
import com.aigreentick.services.template.dto.FacebookTemplateResponse;
import com.aigreentick.services.template.dto.TemplateRequestDto;
import com.aigreentick.services.whatsapp.dto.FacebookDeleteTemplateResponse;
import com.aigreentick.services.whatsapp.dto.FacebookTemplateRequest;
import com.aigreentick.services.whatsapp.dto.SendMessageResult;
import com.aigreentick.services.whatsapp.exceptions.WhatsAppMessageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhatsappServiceImpl {
  private final RestTemplate restTemplate;

  public WhatsappServiceImpl(@Qualifier("metaRestTemplate") RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Value("${whatsapp.base-url}")
  private String baseUrl;

  public Map<String, Object> sendMessage(String mobile, String message) {
    try {
      Thread.sleep(500 + new Random().nextInt(300)); // Simulate 200ms–500ms delay
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    Map<String, Object> response = new HashMap<>();
    response.put("status", MessageStatusEnum.DELIVERED);
    response.put("messageId", UUID.randomUUID().toString());
    response.put("waId", UUID.randomUUID().toString());
    return response;
  }

  public FacebookTemplateResponse submitTemplateToFacebook(TemplateRequestDto dto) {
    String url = String.format("%s/%s/%s/message_templates",
        baseUrl, dto.getApiVersion(), dto.getWabaId());

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(dto.getAccessToken());
    headers.setContentType(MediaType.APPLICATION_JSON);

    try {
      FacebookTemplateRequest requestBody = buildRequestBody(dto);
      ObjectMapper objectMapper = new ObjectMapper();
      String payload = objectMapper.writeValueAsString(requestBody);
      HttpEntity<String> request = new HttpEntity<>(payload, headers);
      ResponseEntity<FacebookTemplateResponse> response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          request,
          FacebookTemplateResponse.class);

      log.info("Template creation response: {}", response.getBody());
      return response.getBody();
    } catch (HttpClientErrorException ex) {
      log.error("Error creating template: {}", ex.getResponseBodyAsString());
      throw ex;
    } catch (JsonProcessingException e) {
      log.error("Error processing JSON: {}", e.getMessage());
      throw new RuntimeException("Error processing JSON", e);
    } catch (Exception e) {
      log.error("Unexpected error: {}", e.getMessage());
      throw new RuntimeException("Unexpected error occurred while submitting template", e);

    }

  }

  private FacebookTemplateRequest buildRequestBody(TemplateRequestDto dto) {
    FacebookTemplateRequest requestBody = new FacebookTemplateRequest();
    requestBody.setName(dto.getName());
    requestBody.setLanguage(dto.getLanguage());
    requestBody.setCategory(dto.getCategory());
    requestBody.setComponents(dto.getComponents());
    return requestBody;
  }

  public boolean deleteTemplateFromFacebook(String wabaId, String templateName, String accessToken, String apiVersion) {
    String url = String.format("https://graph.facebook.com/%s/%s/message_templates?name=%s",
        apiVersion, wabaId, templateName);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    HttpEntity<Void> request = new HttpEntity<>(headers);

    try {
      ResponseEntity<FacebookDeleteTemplateResponse> response = restTemplate.exchange(
          url,
          HttpMethod.DELETE,
          request,
          FacebookDeleteTemplateResponse.class);

      return response.getBody() != null && response.getBody().isSuccess();

    } catch (HttpClientErrorException e) {
      log.error("Failed to delete template '{}': {}", templateName, e.getResponseBodyAsString());
      throw new RuntimeException("Failed to delete WhatsApp template: " + e.getResponseBodyAsString());
    }
  }

  public SendMessageResult sendTemplateMessage(
      String recipientNumber,
      String templateName,
      String languageCode,
      String parametersJson,
      String accessToken,
      String apiVersion,
      String whatsappId) {

    String url = String.format("https://graph.facebook.com/%s/%s/messages", apiVersion, whatsappId);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    headers.setContentType(MediaType.APPLICATION_JSON);

    String payload = buildTemplatePayload(recipientNumber, templateName, languageCode, parametersJson);
    HttpEntity<String> request = new HttpEntity<>(payload, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode body = mapper.readTree(response.getBody());
        String messageId = body.path("messages").get(0).path("id").asText();
        return new SendMessageResult(true, messageId, "Message sent successfully");
      } else {
        return new SendMessageResult(false, null, "Non-success HTTP status: " + response.getStatusCode());
      }

    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      log.error("WhatsApp API client/server error: {}", ex.getResponseBodyAsString());
      throw new WhatsAppMessageException("Failed to send message: " + ex.getResponseBodyAsString());
    } catch (Exception ex) {
      log.error("Unexpected error during message send", ex);
      throw new WhatsAppMessageException("Unexpected error occurred while sending message");
    }
  }

  private String buildTemplatePayload(String recipientNumber, String templateName, String languageCode,
      String parametersJson) {
    return String.format("""
            {
              "messaging_product": "whatsapp",
              "to": "%s",
              "type": "template",
              "template": {
                "name": "%s",
                "language": {
                  "code": "%s"
                },
                "components": [
                  {
                    "type": "body",
                    "parameters": %s
                  }
                ]
              }
            }
        """, recipientNumber, templateName, languageCode, parametersJson);
  }

  public Map<String, Object> submitTemplateToFacebookForApproval(String jsonString) {
    Map<String, Object> response = new HashMap<>();
    response.put("id", "3784378");
    response.put("category", "MARKETING");
    response.put("status", "APPROVED");
    return response;
  }
}
