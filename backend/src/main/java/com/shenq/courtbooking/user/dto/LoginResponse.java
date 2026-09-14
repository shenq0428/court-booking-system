package com.shenq.courtbooking.user.dto;

import com.shenq.courtbooking.user.entity.UserRole;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String name,
        String email,
        UserRole role
) {
}