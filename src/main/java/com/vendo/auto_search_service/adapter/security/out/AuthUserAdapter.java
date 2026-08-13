package com.vendo.auto_search_service.adapter.security.out;

import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.security_starter.context.SecurityContextHelper;
import org.springframework.stereotype.Component;

@Component
public class AuthUserAdapter implements AuthUserPort {

    @Override
    public User getAuthUser() {
        return SecurityContextHelper.getAuthFromContext(User.class);
    }
}
