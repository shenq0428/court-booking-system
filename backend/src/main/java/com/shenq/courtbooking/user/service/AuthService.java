package com.shenq.courtbooking.user.service;

import com.shenq.courtbooking.common.exception.EmailAlreadyRegisteredException;
import com.shenq.courtbooking.user.dto.RegisterRequest;
import com.shenq.courtbooking.user.dto.RegisterResponse;
import com.shenq.courtbooking.user.entity.AppUser;
import com.shenq.courtbooking.user.entity.UserRole;
import com.shenq.courtbooking.user.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService{
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder){
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;  
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request){
        String normalizedName= request.name().trim();
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        //检查重复email
        if(appUserRepository.existsByEmailIgnoreCase(normalizedEmail)){
            throw new EmailAlreadyRegisteredException(
                "Email is already registerd T_T" 
            );
        }

        String passwordHash = passwordEncoder.encode(request.password());

        AppUser appUser = new AppUser(normalizedName,normalizedEmail,passwordHash,UserRole.CUSTOMER);
        
        AppUser savedUser = appUserRepository.save(appUser);

        return new RegisterResponse(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
    }

}
//理解从下到下的逻辑 重点！！！