package com.aigreentick.services.messaging.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.DateNameFilterDto;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.messaging.dto.tag.TagRequestDto;
import com.aigreentick.services.messaging.dto.tag.TagResponseDto;
import com.aigreentick.services.messaging.service.impl.TagServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
public class TagController {
    private final TagServiceImpl tagService;

    @PostMapping("/create")
    public ResponseEntity<?> createTag(@RequestBody TagRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        TagResponseDto responseDto = tagService.createTag(request, loggedUser.getId());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Tag created successfully", responseDto));
    }

    @GetMapping("/my-tags")
    public ResponseEntity<?> getUserTags(
            @RequestParam(name = "search", required = false) String search,
            @Validated @ModelAttribute DateNameFilterDto filter,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        List<TagResponseDto> responseDto = tagService.getUserTags(loggedUser.getId(), search,filter.getFrom(),filter.getTo());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Tag Of User Fetched successfully", responseDto));
                
    }

    @GetMapping("/my-tags/{number}")
    public ResponseEntity<?> getTagsByNumber(
        @PathVariable("number") String number,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        List<TagResponseDto> responseDto = tagService.getTagsLinkedToNumber(loggedUser.getId(), number);
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Tag fetched associate to number: "+number, responseDto));
                
    }

}
