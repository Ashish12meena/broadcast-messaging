package com.aigreentick.services.auth.service.interfaces;

import java.util.Optional;

import com.aigreentick.services.auth.model.User;
import com.aigreentick.services.auth.service.impl.CustomUserDetails;

public interface UserService {
     CustomUserDetails getCurrentUser();
     User findByEmail(String email);
     boolean isMobileNumberExist(String mobileNumber);
     Optional<User> findById(Long id);
     User getReferenceById(Long userId);
     boolean existsById(Long userId);
}
