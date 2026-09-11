package com.shenq.courtbooking.user.repository;

import com.shenq.courtbooking.user.entity.AppUser;
import com.shenq.courtbooking.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void shouldSaveAndFindUserByEmailIgnoringCase() {
        // Arrange：准备测试资料
        AppUser user = new AppUser(
                "Repository Test User",
                "repository.test@courtflow.local",
                "temporary-password-hash",
                UserRole.CUSTOMER
        );

        // Act：真正保存到数据库
        AppUser savedUser =
                appUserRepository.saveAndFlush(user);

        Optional<AppUser> foundUser =
                appUserRepository.findByEmailIgnoreCase(
                        "REPOSITORY.TEST@COURTFLOW.LOCAL"
                );

        // Assert：确认结果正确
        assertNotNull(savedUser.getId());
        assertTrue(foundUser.isPresent());

        assertEquals(
                "Repository Test User",
                foundUser.get().getName()
        );

        assertEquals(
                UserRole.CUSTOMER,
                foundUser.get().getRole()
        );

        assertTrue(
                appUserRepository.existsByEmailIgnoreCase(
                        "repository.test@courtflow.local"
                )
        );
    }
}