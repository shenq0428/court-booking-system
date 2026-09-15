package com.shenq.courtbooking.security;

import com.shenq.courtbooking.user.entity.AppUser;
import com.shenq.courtbooking.user.entity.RefreshToken;
import com.shenq.courtbooking.user.repository.RefreshTokenRepository;
import com.shenq.courtbooking.common.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {
        private static final int TOKEN_BYTE_LENGTH = 32;
        private final RefreshTokenRepository refreshTokenRepository;
        private final Duration refreshTokenTtl;
        private final SecureRandom secureRandom = new SecureRandom();

        public RefreshTokenService(
                        RefreshTokenRepository refreshTokenRepository,
                        @Value("${app.security.jwt.refresh-token-ttl}") Duration refreshTokenTtl) {
                this.refreshTokenRepository = refreshTokenRepository;
                this.refreshTokenTtl = refreshTokenTtl;
        }

        @Transactional
        public String createForUser(AppUser user) {
                String rawToken = generateRawToken();
                String tokenHash = hashToken(rawToken);

                Instant expiresAt = Instant.now().plus(refreshTokenTtl);

                RefreshToken refreshToken = new RefreshToken(
                                user,
                                tokenHash,
                                UUID.randomUUID(),
                                expiresAt);
                refreshTokenRepository.save(refreshToken);
                return rawToken;
        }

        private String generateRawToken() {
                byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];

                secureRandom.nextBytes(randomBytes);

                return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        }

        public String hashToken(String rawToken) {
                try {
                        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                        byte[] hashBytes = messageDigest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

                        return HexFormat.of().formatHex(hashBytes);
                } catch (NoSuchAlgorithmException exception) {
                        throw new IllegalStateException("SHA-256 algorithm is unavailable", exception);
                }
        }

        public Long getRefreshTokenExpiresInSeconds() {
                return refreshTokenTtl.toSeconds();
        }

        @Transactional
        public RefreshTokenRotation rotate(String rawToken) {
                if (rawToken == null || rawToken.isBlank()) {
                        throw new InvalidCredentialsException( "Invalid refresh token");
                }

                String tokenHash = hashToken(rawToken);

                RefreshToken currentToken = refreshTokenRepository.findByTokenHash(tokenHash)
                                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

                Instant now = Instant.now();

                if (currentToken.isRevoked() ||
                                currentToken.isExpired(now) ||
                                !currentToken.getUser().isActive()) {
                        throw new InvalidCredentialsException("Invalid refresh token");
                }

                currentToken.revoke(now);
                refreshTokenRepository.save(currentToken);

                String newRawToken = generateRawToken();
                String newTokenHash = hashToken(newRawToken);

                RefreshToken newRefreshToken = new RefreshToken(
                                currentToken.getUser(),
                                newTokenHash,
                                currentToken.getFamilyId(),
                                now.plus(refreshTokenTtl));

                refreshTokenRepository.save(newRefreshToken);

                return new RefreshTokenRotation(
                                currentToken.getUser(),
                                newRawToken,
                                refreshTokenTtl.toSeconds());
        }

}