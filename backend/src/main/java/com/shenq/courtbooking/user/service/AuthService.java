package com.shenq.courtbooking.user.service;

import com.shenq.courtbooking.common.exception.EmailAlreadyRegisteredException;
import com.shenq.courtbooking.user.dto.RegisterRequest;
import com.shenq.courtbooking.user.dto.RegisterResponse;
import com.shenq.courtbooking.user.entity.AppUser;
import com.shenq.courtbooking.user.entity.UserRole;
import com.shenq.courtbooking.user.repository.AppUserRepository;
import com.shenq.courtbooking.common.exception.InvalidCredentialsException;
import com.shenq.courtbooking.security.JwtService;
import com.shenq.courtbooking.user.dto.LoginRequest;
import com.shenq.courtbooking.user.dto.LoginResponse;
import com.shenq.courtbooking.user.dto.CurrentUserResponse;
import com.shenq.courtbooking.security.RefreshTokenService;
import com.shenq.courtbooking.security.RefreshTokenRotation;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {
        private final AppUserRepository appUserRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final RefreshTokenService refreshTokenService;

        public AuthService(
                        AppUserRepository appUserRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        RefreshTokenService refreshTokenService) {
                this.appUserRepository = appUserRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.refreshTokenService = refreshTokenService;
        }

        @Transactional
        public RegisterResponse register(RegisterRequest request) {
                String normalizedName = request.name().trim();
                String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

                // 检查重复email
                if (appUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
                        throw new EmailAlreadyRegisteredException("Email is already registerd T_T");
                }

                String passwordHash = passwordEncoder.encode(request.password());

                AppUser appUser = new AppUser(normalizedName, normalizedEmail, passwordHash, UserRole.CUSTOMER);

                AppUser savedUser = appUserRepository.save(appUser);

                return new RegisterResponse(
                                savedUser.getId(),
                                savedUser.getName(),
                                savedUser.getEmail(),
                                savedUser.getRole());
        }

        @Transactional
        public LoginResult login(LoginRequest request) {
                String normalizedEmail = request
                                .email()
                                .trim()
                                .toLowerCase(Locale.ROOT);

                AppUser appUser = appUserRepository
                                .findByEmailIgnoreCase(normalizedEmail)
                                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

                if (!appUser.isActive()) {
                        throw new InvalidCredentialsException("Invalid email or password");
                }

                boolean passwordMatches = passwordEncoder.matches(
                                request.password(),
                                appUser.getPasswordHash());

                if (!passwordMatches) {
                        throw new InvalidCredentialsException("Invalid email or password");
                }

                String accessToken = jwtService.generateAccessToken(appUser);
                String refreshToken = refreshTokenService.createForUser(appUser);

                LoginResponse response = new LoginResponse(
                                accessToken,
                                "Bearer",
                                jwtService.getAccessTokenExpiresInSeconds(),
                                appUser.getId(),
                                appUser.getName(),
                                appUser.getEmail(),
                                appUser.getRole());

                return new LoginResult(
                                response,
                                refreshToken,
                                refreshTokenService.getRefreshTokenExpiresInSeconds());
        }

        @Transactional(readOnly = true)
        public CurrentUserResponse getCurrentUser(Long userId) {
                AppUser appUser = appUserRepository
                                .findById(userId)
                                .orElseThrow(() -> new InvalidCredentialsException("User account is unavailable"));
                if (!appUser.isActive()) {
                        throw new InvalidCredentialsException("User account is  unavailable");
                }

                return new CurrentUserResponse(
                                appUser.getId(),
                                appUser.getName(),
                                appUser.getEmail(),
                                appUser.getRole());

        }

        @Transactional
        public LoginResult refresh(String rawRefreshToken) {
                RefreshTokenRotation rotation = refreshTokenService.rotate(rawRefreshToken);

                AppUser appUser = rotation.user();

                String accessToken = jwtService.generateAccessToken(appUser);

                LoginResponse response = new LoginResponse(
                                accessToken,
                                "Bearer",
                                jwtService.getAccessTokenExpiresInSeconds(),
                                appUser.getId(),
                                appUser.getName(),
                                appUser.getEmail(),
                                appUser.getRole());

                return new LoginResult(
                                response,
                                rotation.refreshToken(),
                                rotation.refreshTokenExpiresInSeconds());
        }

        @Transactional
        public void logout(String rawRefreshToken){
                refreshTokenService.revoke(rawRefreshToken);
        }

}
// 理解从下到下的逻辑 重点！！！