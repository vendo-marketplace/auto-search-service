package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.auto_search.exception.InvalidExpirationDateException;
import com.vendo.auto_search_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.auto_search_service.domain.category.exception.CategoryTypeException;
import com.vendo.auto_search_service.domain.category.Category;
import com.vendo.auto_search_service.domain.category.CategoryType;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.domain.user.exception.UserNotOwnerException;
import com.vendo.auto_search_service.infrastructure.props.ExpirationDateProps;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.category.CategoryQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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
    private CategoryQueryPort categoryQueryPort;
    @Mock
    private AuthUserPort authUserPort;
    @Mock
    private ExpirationDateProps expirationDateProps;

    @BeforeEach
    void setUp() {
        lenient().when(expirationDateProps.getMinDays()).thenReturn(1);
        lenient().when(expirationDateProps.getMaxDays()).thenReturn(7);
        lenient().when(authUserPort.getAuthUser()).thenReturn(UserDataBuilder.withAllFields().build());
        lenient().when(categoryQueryPort.findById(anyString())).thenReturn(childCategory());
    }

    private static Category childCategory() {
        return Category.builder().id("category-id").type(CategoryType.CHILD).build();
    }

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

        verify(categoryQueryPort).findById(request.categoryId());

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
        assertThat(saved.notifiedProducts()).isEqualTo(Set.of());
    }

    @Test
    void create_shouldDefaultExpirationDate_whenNotProvided() {
        User authUser = UserDataBuilder.withAllFields().build();
        AutoSearch request = AutoSearchDataBuilder.withAllFields()
                .expirationDate(null)
                .build();

        when(authUserPort.getAuthUser()).thenReturn(authUser);

        commandService.create(request);

        ArgumentCaptor<AutoSearch> captor = ArgumentCaptor.forClass(AutoSearch.class);
        verify(commandPort).save(captor.capture());

        LocalDateTime expected = LocalDateTime.now().plusDays(7);
        assertThat(captor.getValue().expirationDate()).isCloseTo(expected, within(5, ChronoUnit.SECONDS));
    }

    @Test
    void create_shouldThrow_whenExpirationDateOutOfRange() {
        AutoSearch request = AutoSearchDataBuilder.withAllFields()
                .expirationDate(LocalDateTime.now().plusDays(30))
                .build();

        assertThatThrownBy(() -> commandService.create(request))
                .isInstanceOf(InvalidExpirationDateException.class);

        verify(commandPort, never()).save(any());
    }

    @Test
    void create_shouldPropagateCategoryNotFound() {
        AutoSearch request = AutoSearchDataBuilder.withAllFields().build();

        when(categoryQueryPort.findById(request.categoryId()))
                .thenThrow(new CategoryNotFoundException("Category not found."));

        assertThatThrownBy(() -> commandService.create(request))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(commandPort, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenCategoryTypeIsNotChild() {
        AutoSearch request = AutoSearchDataBuilder.withAllFields().build();

        when(categoryQueryPort.findById(request.categoryId()))
                .thenReturn(Category.builder().id(request.categoryId()).type(CategoryType.PARENT).build());

        assertThatThrownBy(() -> commandService.create(request))
                .isInstanceOf(CategoryTypeException.class);

        verify(commandPort, never()).save(any());
    }

    @Test
    void update_shouldUpdateRequest_whenOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);

        commandService.update(existing.id(), update);

        verify(authUserPort).validateAuthOwner(existing.userId());
        verify(categoryQueryPort).findById(update.categoryId());
        verify(commandPort).update(existing.id(), update);
    }

    @Test
    void update_shouldSkipCategoryValidation_whenCategoryIdNotProvided() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().categoryId(null).build();

        when(queryPort.findById(existing.id())).thenReturn(existing);

        commandService.update(existing.id(), update);

        verify(categoryQueryPort, never()).findById(anyString());
        verify(commandPort).update(existing.id(), update);
    }

    @Test
    void update_shouldSkipExpirationValidation_whenExpirationDateNotProvided() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().expirationDate(null).build();

        when(queryPort.findById(existing.id())).thenReturn(existing);

        commandService.update(existing.id(), update);

        verify(commandPort).update(existing.id(), update);
    }

    @Test
    void update_shouldPropagateNotOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().userId("owner-id").build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);
        doThrow(new UserNotOwnerException("You're not owner."))
                .when(authUserPort).validateAuthOwner(existing.userId());

        assertThatThrownBy(() -> commandService.update(existing.id(), update))
                .isInstanceOf(UserNotOwnerException.class);

        verify(commandPort, never()).update(anyString(), any());
    }

    @Test
    void update_shouldPropagateCategoryNotFound() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch update = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);
        when(categoryQueryPort.findById(update.categoryId()))
                .thenThrow(new CategoryNotFoundException("Category not found."));

        assertThatThrownBy(() -> commandService.update(existing.id(), update))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(commandPort, never()).update(anyString(), any());
    }

    @Test
    void delete_shouldDeleteRequest_whenOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(existing.id())).thenReturn(existing);

        commandService.delete(existing.id());

        verify(commandPort).delete(existing.id());
        verify(commandPort, never()).update(anyString(), any());
    }

    @Test
    void delete_shouldPropagateNotOwner() {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields().userId("owner-id").build();

        when(queryPort.findById(existing.id())).thenReturn(existing);
        doThrow(new UserNotOwnerException("You're not owner."))
                .when(authUserPort).validateAuthOwner(existing.userId());

        assertThatThrownBy(() -> commandService.delete(existing.id()))
                .isInstanceOf(UserNotOwnerException.class);

        verify(commandPort, never()).delete(anyString());
    }
}
