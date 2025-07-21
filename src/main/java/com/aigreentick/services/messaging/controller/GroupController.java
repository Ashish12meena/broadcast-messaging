package com.aigreentick.services.messaging.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.messaging.dto.group.GroupRequestDto;
import com.aigreentick.services.messaging.dto.group.GroupResponseDto;
import com.aigreentick.services.messaging.dto.group.GroupResponseForUserDto;
import com.aigreentick.services.messaging.service.impl.GroupServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group")
public class GroupController {
    private final GroupServiceImpl groupService;

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestBody GroupRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        GroupResponseDto responseDto = groupService.createGroup(request, loggedUser.getId());
        return ResponseEntity
                .ok(new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Group create successfully", responseDto));
    }

    @GetMapping("/my-groups")
    public ResponseEntity<?> getUserGroups(@RequestBody GroupRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        List<GroupResponseForUserDto> responseDto = groupService.getUserGroups(loggedUser.getId());
        return ResponseEntity.ok(
                new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "User's Group fetched successfully", responseDto));
    }

    @GetMapping("/delete/{id}")
    public ResponseMessage<?> getUserGroups(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        boolean isDeleted = groupService.deleteGroup(id, loggedUser.getId());
        if (isDeleted) {
            return  new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Group deleted successfully successfully",
                            null);
        }
        return new ResponseMessage<>(ResponseStatus.ERROR.name(), "Unable to delete group", null);
    }
}
