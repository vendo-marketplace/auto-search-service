package com.vendo.auto_search_service.application.auto_search.validation;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.auto_search.exception.CategoryNotFoundException;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSearchValidationFacadeTest {

    @InjectMocks
    private AutoSearchValidationFacade validationFacade;

    @Mock
    private AuthUserPort authUserPort;

    @Test
    void validateOwner_shouldPass_whenAuthUserIsOwner() {
        User authUser = UserDataBuilder.withAllFields().build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().userId(authUser.id()).build();

        when(authUserPort.getAuthUser()).thenReturn(authUser);

        assertThatCode(() -> validationFacade.validateOwner(autoSearch)).doesNotThrowAnyException();
    }

    @Test
    void validateOwner_shouldThrow_whenAuthUserIsNotOwner() {
        User authUser = UserDataBuilder.withAllFields().build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().userId("another-user-id").build();

        when(authUserPort.getAuthUser()).thenReturn(authUser);

        assertThatThrownBy(() -> validationFacade.validateOwner(autoSearch))
                .isInstanceOf(AutoSearchNotFoundException.class);
    }

    @Test
    void validateCategoryExists_shouldPass_whenCategoryIdNotBlank() {
        assertThatCode(() -> validationFacade.validateCategoryExists("category-id")).doesNotThrowAnyException();
    }

    @Test
    void validateCategoryExists_shouldThrow_whenCategoryIdBlank() {
        assertThatThrownBy(() -> validationFacade.validateCategoryExists(" "))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
