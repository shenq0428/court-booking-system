package com.shenq.courtbooking.user.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name= "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY,optional = false)
    @JoinColumn(name="user_id",nullable = false)
    private AppUser user;

    @Column(
        name = "token_hash",
        nullable = false,
        unique = true,
        length = 64
    )
    private String tokenHash;

    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    protected RefreshToken(){}

    public RefreshToken(
        AppUser user,
        String tokenHash,
        UUID familyId,
        Instant expiresAt
    ){
        this.user = user;
        this.tokenHash = tokenHash;
        this.familyId = familyId;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public AppUser getUser() {
        return user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void revoke(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }
}
