package com.vendo.auto_search_service.domain.user;

import com.vendo.user_lib.type.ProviderType;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;

import java.time.Instant;
import java.util.Set;

public class UserDataBuilder {

    public static User.UserBuilder withAllFields() {
        return User.builder()
                .id("user-id")
                .email("test@gmail.com")
                .emailVerified(true)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(UserRole.USER))
                .providerType(ProviderType.LOCAL)
                .createdAt(Instant.now())
                .updatedAt(Instant.now());
    }
}
