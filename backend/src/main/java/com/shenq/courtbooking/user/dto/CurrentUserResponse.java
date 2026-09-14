package com.shenq.courtbooking.user.dto;

import com.shenq.courtbooking.user.entity.UserRole;

public record CurrentUserResponse(
        Long id,
        String name,
        String email,
        UserRole role
) {
}