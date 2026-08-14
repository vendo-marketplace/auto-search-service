package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoSearchQueryServiceTest {

    @InjectMocks
    private AutoSearchQueryService queryService;

    @Mock
    private AutoSearchQueryPort queryPort;
    @Mock
    private AutoSearchValidationPort validationPort;
    @Mock
    private AuthUserPort authUserPort;

    @Test
    void getUserRequests_shouldReturnRequestsForAuthUser() {
        User authUser = UserDataBuilder.withAllFields().build();
        List<AutoSearch> requests = List.of(AutoSearchDataBuilder.withAllFields().build());

        when(authUserPort.getAuthUser()).thenReturn(authUser);
        when(queryPort.findByUserId(authUser.id())).thenReturn(requests);

        List<AutoSearch> result = queryService.getUserRequests();

        assertThat(result).isEqualTo(requests);
        verify(queryPort).findByUserId(authUser.id());
    }

    @Test
    void getById_shouldReturnRequest_whenOwner() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(autoSearch.id())).thenReturn(autoSearch);

        AutoSearch result = queryService.getById(autoSearch.id());

        assertThat(result).isEqualTo(autoSearch);
        verify(validationPort).validateOwner(autoSearch);
    }

    @Test
    void getById_shouldThrow_whenNotOwner() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();

        when(queryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        doThrow(new AutoSearchNotFoundException("Auto search request not found."))
                .when(validationPort).validateOwner(autoSearch);

        assertThatThrownBy(() -> queryService.getById(autoSearch.id()))
                .isInstanceOf(AutoSearchNotFoundException.class);
    }
}
