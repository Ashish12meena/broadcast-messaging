package com.aigreentick.services.messaging.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.exception.PermissionNotFoundException;
import com.aigreentick.services.auth.exception.UserNotFoundException;
import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.interfaces.UserService;
import com.aigreentick.services.messaging.dto.group.GroupMemberDto;
import com.aigreentick.services.messaging.dto.group.GroupMemberRemoveResponseDto;
import com.aigreentick.services.messaging.mapper.GroupMembersMapper;
import com.aigreentick.services.messaging.model.group.Group;
import com.aigreentick.services.messaging.model.group.GroupMembers;
import com.aigreentick.services.messaging.repository.GroupMembersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupMembersServiceImpl {

    private final GroupMembersRepository groupMembersRepository;
    private final GroupServiceImpl groupService;
    private final UserService userService;
    private final GroupMembersMapper groupMemberMapper;

    /**
     * Adds a user to a group by their IDs.
     */
    @Transactional
    public GroupMemberDto addMemberToGroupByOwner(Long groupId, Long memberUserId, Long ownerId) {
        Group group = groupService.getGroupById(groupId);

        User member = userService.findById(memberUserId)
                .orElseThrow(() -> new UserNotFoundException("Can't add to member User Not found"));

        User owner = userService.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException("Can't add to member User Not found"));

        boolean alreadyExists = groupMembersRepository.existsByGroupAndMemberUserAndDeletedAtIsNull(group, member);
        if (alreadyExists) {
            throw new IllegalArgumentException("User is already a member of this group");
        }

        if (group.getOwner().getId() == ownerId) {
            GroupMembers groupMember = GroupMembers.builder()
                    .group(group)
                    .memberUser(member)
                    .addedBy(owner)
                    .createdAt(LocalDateTime.now())
                    .build();

            GroupMembers savedMember = groupMembersRepository.save(groupMember);
            log.info("User {} added to group {}", memberUserId, groupId);
            return groupMemberMapper.toGroupMemberDto(savedMember);
        } else {
            throw new PermissionNotFoundException("You don't have a permission to add User");
        }
    }

    /**
     * Soft-deletes a group member.
     */
    @Transactional
    public boolean removeMemberFromGroup(Long groupId, Long groupMemberId,Long ownerId) {
        Optional<GroupMembers> optionalGroupMember = groupMembersRepository.findByIdAndGroup_Id(groupMemberId, groupId);
        GroupMembers groupMember;
        if (optionalGroupMember.isPresent()) {
            groupMember = optionalGroupMember.get();
            if (groupMember.getGroup().getOwner().getId() == ownerId) {
                groupMembersRepository.deleteById(groupMemberId);
                log.info("User {} removed from group {}",groupMember.getMemberUser().getUsername(),groupMember.getGroup().getGroupName());
            } else {
                throw new PermissionNotFoundException("You don't have a permission to remove User");
            }
        }
        return true;
    }

    /**
     * Removes all members of a group (soft-delete).
     */
    @Transactional
    public List<GroupMemberRemoveResponseDto> removeAllMembersFromGroup(Long groupId,Long ownerId) {
        Group group = groupService.getGroupById(groupId);
        if (group.getOwner().getId()==ownerId) {
            List<GroupMembers> members = groupMembersRepository.deleteAllByGroup_Id(groupId); 
           return members.stream().map(member->
            new GroupMemberRemoveResponseDto(member.getAddedBy().getUsername(),member.getMemberUser().getUsername())).toList();
        }
        log.info("All members removed from group {}", groupId);
        return null;
    }
}
