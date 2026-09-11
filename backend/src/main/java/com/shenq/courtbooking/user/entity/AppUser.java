package com.shenq.courtbooking.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name="app_users",uniqueConstraints={ @UniqueConstraint(name="uk_app_users_email",columnNames="email")})

public class AppUser {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false,length=100)
    private String name;

    @Column(nullable=false,length=100)
    private String email;

    @Column(name="password_hash",nullable=false,length=100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false,length=20)
    private UserRole role;

    @Column(nullable=false)
    private boolean active = true;

    protected AppUser(){}

    public AppUser(String name, String email, String passwordHash, UserRole role){
        this.name=name;
        this.email=email;
        this.passwordHash = passwordHash;
        this.role=role;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public UserRole getRole() {
        return role;
    }
     public boolean isActive() {
        return active;
    }
}
