package com.shenq.courtbooking.user.repository;

import com.shenq.courtbooking.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByFamilyIdAndRevokedAtIsNull(UUID familyId);
}   
