package com.shenq.courtbooking.user.controller;

import com.shenq.courtbooking.user.dto.RegisterRequest;
import com.shenq.courtbooking.user.dto.RegisterResponse;
import com.shenq.courtbooking.user.service.AuthService;
import com.shenq.courtbooking.user.dto.LoginRequest;
import com.shenq.courtbooking.user.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.shenq.courtbooking.user.dto.CurrentUserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(
        @Valid @RequestBody LoginRequest request){
            LoginResponse response = authService.login(request);
            
            return ResponseEntity.ok(response);
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