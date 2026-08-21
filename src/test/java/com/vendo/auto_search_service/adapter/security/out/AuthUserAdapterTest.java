package com.vendo.auto_search_service.adapter.security.out;

import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.domain.user.exception.UserNotOwnerException;
import com.vendo.security_starter.context.SecurityContextHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class AuthUserAdapterTest {

    private final AuthUserAdapter adapter = new AuthUserAdapter();

    private MockedStatic<SecurityContextHelper> securityContextHelper;

    @AfterEach
    void tearDown() {
        if (securityContextHelper != null) {
            securityContextHelper.close();
        }
    }

    @Test
    void validateAuthOwner_shouldPass_whenOwner() {
        User authUser = UserDataBuilder.withAllFields().id("user-id").build();
        mockAuthUser(authUser);

        assertThatCode(() -> adapter.validateAuthOwner("user-id")).doesNotThrowAnyException();
    }

    @Test
    void validateAuthOwner_shouldThrow_whenNotOwner() {
        User authUser = UserDataBuilder.withAllFields().id("user-id").build();
        mockAuthUser(authUser);

        assertThatThrownBy(() -> adapter.validateAuthOwner("another-user-id"))
                .isInstanceOf(UserNotOwnerException.class);
    }

    private void mockAuthUser(User authUser) {
        securityContextHelper = Mockito.mockStatic(SecurityContextHelper.class);
        securityContextHelper.when(() -> SecurityContextHelper.getAuthFromContext(User.class)).thenReturn(authUser);
    }
}
