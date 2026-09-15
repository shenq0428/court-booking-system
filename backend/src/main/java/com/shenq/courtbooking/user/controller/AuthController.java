package com.shenq.courtbooking.user.controller;

import com.shenq.courtbooking.user.dto.RegisterRequest;
import com.shenq.courtbooking.user.dto.RegisterResponse;
import com.shenq.courtbooking.user.service.AuthService;
import com.shenq.courtbooking.user.dto.LoginRequest;
import com.shenq.courtbooking.user.dto.LoginResponse;
import com.shenq.courtbooking.user.service.LoginResult;
import com.shenq.courtbooking.user.dto.CurrentUserResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;


import java.time.Duration;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final boolean secureCookie;

    public AuthController(
        AuthService authService,
        @Value("${app.security.cookie.secure}")
        boolean secureCookie
    ) {
        this.authService = authService;
        this.secureCookie = secureCookie;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(
        @Valid @RequestBody LoginRequest request
    ){
            LoginResult result = authService.login(request);
            
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token",result.refreshToken())
            .httpOnly(true).secure(secureCookie).sameSite("Strict").path("/api/auth")
            .maxAge(Duration.ofSeconds(result.refreshTokenExpiresInSeconds())).build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString()).body(result.response());
        }
    
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse>me(
        @AuthenticationPrincipal Jwt jwt)
        {
            Long userId = Long.valueOf(jwt.getSubject());

            CurrentUserResponse response = authService.getCurrentUser(userId);
            
            return ResponseEntity.ok(response);
        }
    
}