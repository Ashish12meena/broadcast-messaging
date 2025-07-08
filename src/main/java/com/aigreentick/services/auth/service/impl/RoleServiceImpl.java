package com.aigreentick.services.auth.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.aigreentick.services.auth.dto.RoleResponseDto;
import com.aigreentick.services.auth.mapper.RoleMapper;
import com.aigreentick.services.auth.model.Role;
import com.aigreentick.services.auth.repository.RoleRepository;
import com.aigreentick.services.auth.service.interfaces.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleResponseDto> getAllRoles() {
        List<Role> roles =  roleRepository.findAll();
       return  roleMapper.toRoleResponseDtoList(roles);
    }
    
}
