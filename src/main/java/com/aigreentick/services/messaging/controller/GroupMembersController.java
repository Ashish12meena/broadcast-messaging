package com.aigreentick.services.messaging.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aigreentick.services.auth.service.impl.CustomUserDetails;
import com.aigreentick.services.common.dto.ResponseMessage;
import com.aigreentick.services.messaging.dto.ResponseStatus;
import com.aigreentick.services.messaging.dto.group.GroupMemberRequestDto;
import com.aigreentick.services.messaging.dto.group.GroupMemberDto;
import com.aigreentick.services.messaging.service.impl.GroupMembersServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/group-member")
public class GroupMembersController {
    private final GroupMembersServiceImpl groupMembersService;

    @PostMapping("/add")
    public ResponseEntity<?> addMemberToGroupByOwner(@RequestBody GroupMemberRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        GroupMemberDto responseDto = groupMembersService.addMemberToGroupByOwner(request.getGroupId(),
                request.getMemberUserId(), loggedUser.getId());
        return ResponseEntity.ok(
                new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Memeber added to group sucessfully",
                        responseDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> removeMemberFromGroup(@RequestBody GroupMemberRequestDto request,
            @AuthenticationPrincipal CustomUserDetails loggedUser) {
        boolean isRemoved = groupMembersService.removeMemberFromGroup(request.getGroupId(),
                request.getMemberUserId(), loggedUser.getId());
        return ResponseEntity.ok(
                new ResponseMessage<>(ResponseStatus.SUCCESS.name(), "Memeber removed from group sucessfully",
                        isRemoved));
    }
}
