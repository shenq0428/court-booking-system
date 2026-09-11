package com.shenq.courtbooking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//what is record here?
public record RegisterRequest(
    @NotBlank(message = "Name is required")
    @Size(max=100,message="Name must not exceed 100 characters")
    String name,

    @NotBlank(message="Email is required")
    @Email(message="Email format is invalid")
    @Size(max=320, message="Email must not exceed 320 character")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min=8,max=72,message="Password must contain between 8 and 72 characters")
    String password

){}

// RegisterRequest
// React → 后端
// name、email、password

// RegisterResponse
// 后端 → React
// id、name、email、role