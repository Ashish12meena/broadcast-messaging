package com.aigreentick.services.template.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.aigreentick.services.template.dto.TemplateComponentButtonRequestDto;
import com.aigreentick.services.template.dto.TemplateComponentRequestDto;
import com.aigreentick.services.template.dto.buildTemplate.TemplateRequestDto;
import com.aigreentick.services.template.enums.TemplateCategory;
import com.aigreentick.services.template.model.Template;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuildTemplateServiceImpl {

    /**
     * Builds the request body JSON for WhatsApp template approval.
     * Delegates component-specific logic to helper methods for clarity.
     */
    public ObjectNode buildRequestBodyForApproval(TemplateRequestDto dto) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        // Basic required fields for WhatsApp template
        root.put("name", dto.getName());
        root.put("language", dto.getLanguage());
        root.put("category", TemplateCategory.valueOf(dto.getCategory()).name());

        ArrayNode componentsArray = mapper.createArrayNode();
        
        // Handle each component based on type
        for (TemplateComponentRequestDto componentDto : dto.getComponents()) {
            String type = componentDto.getType().toUpperCase();

            ObjectNode componentNode = switch (type) {
                case "HEADER" -> buildHeaderComponent(componentDto, mapper);
                case "BODY" -> buildBodyComponent(componentDto, mapper);
                case "FOOTER" -> buildFooterComponent(componentDto, mapper);
                case "BUTTONS" -> buildButtonsComponent(componentDto, mapper);
                default -> throw new IllegalArgumentException("Unsupported component type: " + type);
            };

            componentsArray.add(componentNode);
        }

        root.set("components", componentsArray);
        return root;

    }

    /**
     * Builds HEADER component with TEXT or media (IMAGE/VIDEO/DOCUMENT).
     */
    private ObjectNode buildHeaderComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        if (dto.getFormat() == null) {
            throw new IllegalArgumentException("Header format cannot be null");
        }

        ObjectNode headerNode = mapper.createObjectNode();
        headerNode.put("type", "HEADER");
        String format = dto.getFormat().toUpperCase();
        headerNode.put("format", format);

        ObjectNode example = mapper.createObjectNode();

        if ("TEXT".equalsIgnoreCase(format)) {
            if (dto.getText() == null || dto.getText().isBlank()) {
                throw new IllegalArgumentException("Header text cannot be null or blank for TEXT format");
            }

            headerNode.put("text", dto.getText());

            List<String> placeholders = extractPlaceholderKeys(dto.getText());

            if (!placeholders.isEmpty()) {
                ArrayNode exampleArray = mapper.createArrayNode();

                List<String> textExamples = dto.getTextExamples();
                if (textExamples == null || textExamples.isEmpty()) {
                    for (String placeholder : placeholders) {
                        exampleArray.add("Sample " + placeholder);
                    }
                } else {
                    for (String ex : textExamples) {
                        exampleArray.add(ex);
                    }
                }

                example.set("header_text", exampleArray);
                headerNode.set("example", example);
            }

        } else {
            List<String> mediaUrls = dto.getMediaUrls();
            if (mediaUrls == null || mediaUrls.isEmpty()) {
                throw new IllegalArgumentException(
                        "At least one media URL is required for HEADER with format: " + format);
            }

            ArrayNode mediaExample = mapper.createArrayNode();
            for (String url : mediaUrls) {
                if (url != null && !url.isBlank()) {
                    mediaExample.add(url.trim());
                }
            }

            if (mediaExample.isEmpty()) {
                throw new IllegalArgumentException("Media URLs cannot be blank for HEADER with format: " + format);
            }

            example.set("header_handle", mediaExample);
            headerNode.set("example", example);
        }

        return headerNode;
    }

    /**
     * Builds BODY component with optional placeholder detection.
     */
    private ObjectNode buildBodyComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        if (dto.getText() == null || dto.getText().isBlank()) {
            throw new IllegalArgumentException("Body text cannot be null or blank");
        }

        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.put("type", "BODY");
        bodyNode.put("text", dto.getText());

        List<String> placeholders = extractPlaceholderKeys(dto.getText());

        if (!placeholders.isEmpty()) {
            ObjectNode example = mapper.createObjectNode();
            ArrayNode exampleMatrix = mapper.createArrayNode();

            List<String> bodyExamples = dto.getTextExamples();

            // Build a row of example values matching placeholder count
            ArrayNode row = mapper.createArrayNode();
            if (bodyExamples == null || bodyExamples.size() < placeholders.size()) {
                // Fill with generic "Sample {{n}}"
                for (String placeholder : placeholders) {
                    row.add("Sample " + placeholder);
                }
            } else {
                for (int i = 0; i < placeholders.size(); i++) {
                    row.add(bodyExamples.get(i));
                }
            }

            exampleMatrix.add(row);
            example.set("body_text", exampleMatrix);
            bodyNode.set("example", example);
        }

        return bodyNode;
    }

    /**
     * Builds FOOTER component containing static text.
     */
    private ObjectNode buildFooterComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        ObjectNode footerNode = mapper.createObjectNode();
        footerNode.put("type", "FOOTER");
        footerNode.put("text", dto.getText());
        return footerNode;
    }

    /**
     * Builds BUTTONS component for URL or PHONE_NUMBER types.
     */

    private ObjectNode buildButtonsComponent(TemplateComponentRequestDto dto, ObjectMapper mapper) {
        if (dto.getButtons() == null || dto.getButtons().isEmpty()) {
            throw new IllegalArgumentException("BUTTONS component requires at least one button.");
        }
        
        ObjectNode buttonsComponentNode = mapper.createObjectNode();
        buttonsComponentNode.put("type", "BUTTONS");

        ArrayNode buttonsArray = mapper.createArrayNode();
        ArrayNode exampleButtonUrlValues = mapper.createArrayNode(); // For dynamic URL examples (nested)

        for (TemplateComponentButtonRequestDto btn : dto.getButtons()) {
            if (btn.getType() == null || btn.getText() == null || btn.getText().isBlank()) {
                throw new IllegalArgumentException("Each button must have a non-null 'type' and non-blank 'text'.");
            }

            String type = btn.getType().toUpperCase();

            ObjectNode buttonWrapper = mapper.createObjectNode();
            buttonWrapper.put("type", type);

            ObjectNode buttonPayload = mapper.createObjectNode();
            buttonPayload.put("type", type);
            buttonPayload.put("text", btn.getText());

            switch (type) {
                case "URL":
                    String url = btn.getUrl();
                    if (url == null || url.isBlank()) {
                        throw new IllegalArgumentException("URL button requires a valid 'url'.");
                    }

                    buttonPayload.put("url", url);

                    // Handle dynamic URL example
                    if (url.contains("{{")) {
                        List<String> placeholders = extractPlaceholderKeys(url);
                        if (placeholders.size() != 1) {
                            throw new IllegalArgumentException("WhatsApp allows only one placeholder per URL button.");
                        }

                        List<String> examples = btn.getUrlExamples();
                        if (examples == null || examples.isEmpty()) {
                            throw new IllegalArgumentException("Dynamic URL requires at least one example.");
                        }

                        // ✅ Wrap example in array to make it 2D as required
                        ArrayNode nestedExample = mapper.createArrayNode();
                        nestedExample.add(examples.get(0));
                        exampleButtonUrlValues.add(nestedExample);
                    }
                    break;

                case "PHONE_NUMBER":
                    String phoneNumber = btn.getPhoneNumber();
                    if (phoneNumber == null || phoneNumber.isBlank()) {
                        throw new IllegalArgumentException("PHONE_NUMBER button requires 'phoneNumber'.");
                    }
                    buttonPayload.put("phone_number", phoneNumber);
                    break;

                case "QUICK_REPLY":
                    // No extra payload needed
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported button type: " + type);
            }

            buttonWrapper.set("button", buttonPayload);
            buttonsArray.add(buttonWrapper);
        }

        buttonsComponentNode.set("buttons", buttonsArray);

        // ✅ Only add example if present
        if (exampleButtonUrlValues.size() > 0) {
            ObjectNode exampleNode = mapper.createObjectNode();
            exampleNode.set("button_url", exampleButtonUrlValues); // 2D array
            buttonsComponentNode.set("example", exampleNode);
        }

        return buttonsComponentNode;
    }

    /******************
     * Builld tempalte for sending message
     *****************************/
    public ObjectNode buildTemplateForSending(Template template,
            Map<String, String> parameters,
            String phoneNumber) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();

        root.put("messaging_product", "whatsapp");
        root.put("to", phoneNumber);
        root.put("type", "template");

        ObjectNode templateNode = mapper.createObjectNode();
        templateNode.put("name", template.getName());

        ObjectNode languageNode = mapper.createObjectNode();
        languageNode.put("code", template.getLanguage());
        templateNode.set("language", languageNode);

        ArrayNode componentsArray = mapper.createArrayNode();
        JsonNode componentsJson = template.getComponentsJson();

        for (JsonNode component : componentsJson) {
            String type = getSafeText(component, "type").toUpperCase();
            switch (type) {
                case "HEADER" -> {
                    ObjectNode headerComponent = buildHeaderComponent(component, parameters, mapper);
                    if (headerComponent != null)
                        componentsArray.add(headerComponent);
                }
                case "BODY" -> {
                    ObjectNode bodyComponent = buildBodyComponent(component, parameters, mapper);
                    if (bodyComponent != null)
                        componentsArray.add(bodyComponent);
                }
                case "BUTTONS" -> {
                    List<ObjectNode> buttonComponents = buildButtonsComponent(component, parameters, mapper);
                    buttonComponents.forEach(componentsArray::add);
                }
            }
        }

        templateNode.set("components", componentsArray);
        root.set("template", templateNode);
        return root;
    }

    private ObjectNode buildHeaderComponent(JsonNode component, Map<String, String> parameters, ObjectMapper mapper) {
        String format = getSafeText(component, "format").toUpperCase();
        ObjectNode headerNode = mapper.createObjectNode();
        headerNode.put("type", "header");

        ArrayNode parametersArray = mapper.createArrayNode();

        switch (format) {
            case "TEXT" -> {
                String text = getSafeText(component, "text");
                List<String> keys = extractPlaceholderKeys(text);

                // Only add parameters if placeholders exist
                if (!keys.isEmpty()) {
                    for (String key : keys) {
                        ObjectNode param = mapper.createObjectNode();
                        param.put("type", "text");
                        param.put("text", parameters.getOrDefault(key, ""));
                        parametersArray.add(param);
                    }
                }
            }

            case "IMAGE", "VIDEO", "DOCUMENT" -> {
                String mediaUrl = getSafeText(component, "mediaUrl");
                if (!mediaUrl.isEmpty()) {
                    ObjectNode param = mapper.createObjectNode();
                    param.put("type", format.toLowerCase());

                    ObjectNode mediaNode = mapper.createObjectNode();
                    mediaNode.put("link", mediaUrl);

                    param.set(format.toLowerCase(), mediaNode);
                    parametersArray.add(param);
                } else {
                    // Optional: Log if media URL is empty
                    log.info("Skipping HEADER component: mediaUrl is empty for format " + format);
                }
            }

            default -> {
                log.info("Unsupported header format: " + format);
            }
        }

        if (!parametersArray.isEmpty()) {
            headerNode.set("parameters", parametersArray);
            return headerNode;
        } else {
            return null;
        }
    }

    private ObjectNode buildBodyComponent(JsonNode component, Map<String, String> parameters, ObjectMapper mapper) {
        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.put("type", "body");

        String text = getSafeText(component, "text");
        List<String> keys = extractPlaceholderKeys(text);

        ArrayNode parametersArray = mapper.createArrayNode();
        for (String key : keys) {
            ObjectNode param = mapper.createObjectNode();
            param.put("type", "text");
            param.put("text", parameters.getOrDefault(key, ""));
            parametersArray.add(param);
        }

        if (!parametersArray.isEmpty()) {
            bodyNode.set("parameters", parametersArray);
            return bodyNode;
        } else {
            return null;
        }

    }

    private List<ObjectNode> buildButtonsComponent(JsonNode component, Map<String, String> parameters,
            ObjectMapper mapper) {
        List<ObjectNode> buttonComponents = new ArrayList<>();

        if (component == null || !component.has("buttons") || !component.get("buttons").isArray()) {
            return buttonComponents;
        }

        int index = 0;
        for (JsonNode button : component.get("buttons")) {
            ObjectNode buttonNode = mapper.createObjectNode();
            buttonNode.put("type", "button");

            ObjectNode subComponent = mapper.createObjectNode();
            String type = getSafeText(button, "type").toUpperCase();
            subComponent.put("sub_type", type.toLowerCase());
            subComponent.put("index", String.valueOf(index++));

            ArrayNode parametersArray = mapper.createArrayNode();
            boolean shouldAddParameters = false;

            switch (type) {
                case "QUICK_REPLY" -> {
                    String payload = getSafeText(button, "text");
                    if (!payload.isEmpty()) {
                        ObjectNode paramNode = mapper.createObjectNode();
                        paramNode.put("type", "payload");
                        paramNode.put("payload", payload);
                        parametersArray.add(paramNode);
                        shouldAddParameters = true;
                    }
                }

                case "URL" -> {
                    String url = getSafeText(button, "url");
                    List<String> keys = extractPlaceholderKeys(url);
                    if (!keys.isEmpty()) {
                        for (String key : keys) {
                            url = url.replace("{{" + key + "}}", parameters.getOrDefault(key, ""));
                        }
                        ObjectNode paramNode = mapper.createObjectNode();
                        paramNode.put("type", "text");
                        paramNode.put("text", url);
                        parametersArray.add(paramNode);
                        shouldAddParameters = true;
                    }
                }

                case "PHONE_NUMBER" -> {
                    String phoneNumber = getSafeText(button, "phone_number");
                    List<String> keys = extractPlaceholderKeys(phoneNumber);
                    if (!keys.isEmpty()) {
                        for (String key : keys) {
                            phoneNumber = phoneNumber.replace("{{" + key + "}}", parameters.getOrDefault(key, ""));
                        }
                        ObjectNode paramNode = mapper.createObjectNode();
                        paramNode.put("type", "phone_number");
                        paramNode.put("phone_number", phoneNumber);
                        parametersArray.add(paramNode);
                        shouldAddParameters = true;
                    }
                    // If static phone number, WhatsApp handles it — no parameters needed
                }
            }
            if (shouldAddParameters) {
                subComponent.set("parameters", parametersArray);
            }
            buttonNode.set("button", subComponent);
            buttonComponents.add(buttonNode);
        }

        return buttonComponents;
    }

    private List<String> extractPlaceholderKeys(String bodyText) {
        List<String> keys = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\{\\{(.*?)}}").matcher(bodyText);
        while (matcher.find()) {
            keys.add(matcher.group(1).trim());
        }
        return keys;
    }

    private String getSafeText(JsonNode node, String fieldName) {
        return node != null && node.has(fieldName) && !node.get(fieldName).isNull()
                ? node.get(fieldName).asText()
                : "";
    }

}
