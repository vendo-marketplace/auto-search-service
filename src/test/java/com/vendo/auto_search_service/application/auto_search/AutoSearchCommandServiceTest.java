package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.auto_search.exception.CategoryNotFoundException;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoSearchCommandServiceTest {

    @InjectMocks
    private AutoSearchCommandService commandService;

    @Mock
    private AutoSearchCommandPort commandPort;
    @Mock
    private AutoSearchQueryPort queryPort;
    @Mock
    private AutoSearchValidationPort validationPort;
    @Mock
    private AuthUserPort authUserPort;

    @Test
    void create_shouldSaveActiveRequestForAuthUser() {
        User authUser = UserDataBuilder.withAllFields().build();
        AutoSearch request = AutoSearchDataBuilder.withAllFields()
                .id(null)
                .userId(null)
                .status(null)
                .build();

        when(authUserPort.getAuthUser()).thenReturn(authUser);

        commandService.create(request);

        verify(validationPort).validateCategoryExists(request.categoryId());

        ArgumentCaptor<AutoSearch> captor = ArgumentCaptor.forClass(AutoSearch.class);
        verify(commandPort).save(captor.capture());

        AutoSearch saved = captor.getValue();
        assertThat(saved.id()).isNull();
        assertThat(saved.userId()).isEqualTo(authUser.id());
        assertThat(saved.status()).isEqualTo(SearchStatus.ACTIVE);
        assertThat(saved.categoryId()).isEqualTo(request.categoryId());
        assertThat(saved.minPrice()).isEqualTo(request.minPrice());
        assertThat(saved.maxPrice()).isEqualTo(request.maxPrice());
        assertThat(saved.address()).isEqualTo(request.address());
        assertThat(saved.expirationDate()).isEqualTo(request.expirationDate());
    }

    @Test
    void update_shouldUpdateRequest_whenOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);

        commandService.update(existing.id(), update);

        verify(validationPort).validateOwner(existing);
        verify(validationPort).validateCategoryExists(update.categoryId());
        verify(commandPort).update(existing.id(), update);
    }

    @Test
    void update_shouldSkipCategoryValidation_whenCategoryIdNotProvided() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().categoryId(null).build();

        when(queryPort.findById(existing.id())).thenReturn(existing);

        commandService.update(existing.id(), update);

        verify(validationPort, never()).validateCategoryExists(anyString());
        verify(commandPort).update(existing.id(), update);
    }

    @Test
    void update_shouldThrow_whenNotOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);
        doThrow(new AutoSearchNotFoundException("Auto search request not found."))
                .when(validationPort).validateOwner(existing);

        assertThatThrownBy(() -> commandService.update(existing.id(), update))
                .isInstanceOf(AutoSearchNotFoundException.class);

        verify(commandPort, never()).update(anyString(), any());
    }

    @Test
    void update_shouldPropagateCategoryNotFound() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);
        doThrow(new CategoryNotFoundException("Category not found."))
                .when(validationPort).validateCategoryExists(update.categoryId());

        assertThatThrownBy(() -> commandService.update(existing.id(), update))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(commandPort, never()).update(anyString(), any());
    }

    @Test
    void delete_shouldCancelRequest_whenOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);

        commandService.delete(existing.id());

        verify(validationPort).validateOwner(existing);

        ArgumentCaptor<AutoSearch> captor = ArgumentCaptor.forClass(AutoSearch.class);
        verify(commandPort).update(eq(existing.id()), captor.capture());

        assertThat(captor.getValue().status()).isEqualTo(SearchStatus.CANCELLED);
    }

    @Test
    void delete_shouldThrow_whenNotOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);
        doThrow(new AutoSearchNotFoundException("Auto search request not found."))
                .when(validationPort).validateOwner(existing);

        assertThatThrownBy(() -> commandService.delete(existing.id()))
                .isInstanceOf(AutoSearchNotFoundException.class);

        verify(commandPort, never()).update(anyString(), any());
    }
}
