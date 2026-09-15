package com.shenq.courtbooking.security;

import com.shenq.courtbooking.user.entity.AppUser;

public record RefreshTokenRotation(
    AppUser user,
    String refreshToken,
    long refreshTokenExpiresInSeconds
){}

