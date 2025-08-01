package com.aigreentick.services.template.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.template.constants.TemplateConstants;
import com.aigreentick.services.template.dto.TemplateCreateResponseDto;
import com.aigreentick.services.template.dto.buildTemplate.BaseTemplateRequestDto;
import com.aigreentick.services.template.dto.buildTemplate.TemplateCreateRequestDto;
import com.aigreentick.services.template.enums.TemplateStatus;
import com.aigreentick.services.template.exception.TemplateAlreadyExistsException;
import com.aigreentick.services.template.mapper.TemplateMapper;
import com.aigreentick.services.template.model.Template;
import com.aigreentick.services.template.repository.TemplateRepository;
import com.aigreentick.services.whatsapp.dto.template.FacebookApiResponse;
import com.aigreentick.services.whatsapp.service.impl.WhatsAppTemplateServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationTemplateServiceImpl {
    private final TemplateRepository templateRepository;
    private final WhatsAppTemplateServiceImpl whatsAppTemplateService;
    private final UserService userService;
    private final TemplateMapper templateMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public TemplateCreateResponseDto createTemplate(TemplateCreateRequestDto request, Long userId) {
        // Get user
        User user = userService.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found with id " + userId));

        BaseTemplateRequestDto baseTemplateRequestDto = request.getTemplate();

        // Check for duplicate template template
        checkDuplicateTemplate(baseTemplateRequestDto.getName());

        // Convert ObjectNode to JSON String
        String jsonRequest = serializeTemplate(baseTemplateRequestDto);

        // Call WhatsAppTemplateService
        FacebookApiResponse<JsonNode> fbResponse = whatsAppTemplateService.sendTemplateToFacebook(
                jsonRequest, request.getWabaId(), request.getAccessToken(), request.getApiVersion());

        //  Handle server-side errors (isSuccess = false)
        if (!fbResponse.isSuccess()) {
            return new TemplateCreateResponseDto(false, null, null, null,
                    fbResponse.getErrorMessage(), null);
        }

        JsonNode data = fbResponse.getData();

        if (data.has("error")) {
            // Facebook error payload
            String errorMessage = data.path("error").path("message").asText("Unknown error");
            return new TemplateCreateResponseDto(false, null, null, null, errorMessage, data);
        }

        String templateId = data.path("id").asText(null);
        String status = data.path("status").asText(null);
        String category = data.path("category").asText(null);

        if (templateId == null || status == null) {
            return new TemplateCreateResponseDto(false, null, null, null,
                    "Invalid response from Facebook API", data);
        }

        JsonNode componentsJson = objectMapper.valueToTree(baseTemplateRequestDto.getComponents());


        Template template = templateMapper.toTemplateEntity(user, jsonRequest, baseTemplateRequestDto,componentsJson, data);
        template.setMetaTemplateId(templateId);
        template.setStatus(TemplateStatus.valueOf(status.toUpperCase()));
        template.setCategory(category);
        template.setResponse(data);
        templateRepository.save(template);

        return new TemplateCreateResponseDto(true, templateId, status, category, null, null);

    }

     private void checkDuplicateTemplate(String name) {
        if (templateRepository.findByName(name).isPresent()) {
            throw new TemplateAlreadyExistsException(String.format(TemplateConstants.TEMPLATE_EXISTS_MSG, name));
        }
    }

    private String serializeTemplate(BaseTemplateRequestDto baseDto) {
        try {
            return objectMapper.writeValueAsString(baseDto);
        } catch (Exception e) {
            log.error("Failed to serialize template request to JSON", e);
            throw new IllegalStateException("Template serialization failed");
        }
    }

}
