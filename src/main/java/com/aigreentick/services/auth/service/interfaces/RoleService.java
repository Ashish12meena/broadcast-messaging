package com.aigreentick.services.auth.service.interfaces;

import java.util.List;

import com.aigreentick.services.auth.dto.RoleResponseDto;

public interface RoleService {
     List<RoleResponseDto> getAllRoles();
}