package com.shenq.courtbooking.user.service;

import com.shenq.courtbooking.user.dto.LoginResponse;

public record LoginResult(
        LoginResponse response,
        String refreshToken,
        long refreshTokenExpiresInSeconds
) {
}