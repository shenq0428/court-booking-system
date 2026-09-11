package com.shenq.courtbooking.user.dto;

import com.shenq.courtbooking.user.entity.UserRole;

public record RegisterResponse(
    Long id,
    String name,
    String email,
    UserRole role
){}

// RegisterRequest
// React → 后端
// name、email、password

// RegisterResponse
// 后端 → React
// id、name、email、role
