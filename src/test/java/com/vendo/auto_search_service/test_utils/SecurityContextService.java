package com.vendo.auto_search_service.test_utils;

import com.vendo.auto_search_service.domain.user.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class SecurityContextService {

    public static SecurityContext initializeSecurityContext(User user) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        List<SimpleGrantedAuthority> authorities = user.roles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();

        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities)
        );

        return securityContext;
    }

    public static SecurityContext initializeSecurityContext(AbstractAuthenticationToken authentication) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);

        return securityContext;
    }
}
