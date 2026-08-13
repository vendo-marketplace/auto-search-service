package com.vendo.auto_search_service.domain.user;

import com.vendo.user_lib.type.ProviderType;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record User(

        String id,
        String email,
        boolean emailVerified,
        UserStatus status,
        Set<UserRole> roles,
        ProviderType providerType,
        String password,
        LocalDate birthDate,
        String fullName,
        Instant createdAt,
        Instant updatedAt

) {

    public Set<String> toRoleNames() {
        return roles.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}
