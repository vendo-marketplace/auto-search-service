package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSearchQueryServiceTest {

    @InjectMocks
    private AutoSearchQueryService queryService;

    @Mock
    private AutoSearchQueryPort queryPort;
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
}
