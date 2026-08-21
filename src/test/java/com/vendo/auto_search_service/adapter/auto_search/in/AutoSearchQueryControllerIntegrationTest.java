package com.vendo.auto_search_service.adapter.auto_search.in;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchQueryUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.vendo.auto_search_service.test_utils.SecurityContextService.initializeSecurityContext;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutoSearchQueryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutoSearchQueryUseCase autoSearchQueryUseCase;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        User authUser = UserDataBuilder.withAllFields().build();
        securityContext = initializeSecurityContext(authUser);
    }

    @Nested
    class GetTests {

        @Test
        void getUserRequests_shouldReturnRequests() throws Exception {
            List<AutoSearch> requests = List.of(AutoSearchDataBuilder.withAllFields().build());

            when(autoSearchQueryUseCase.getUserRequests()).thenReturn(requests);

            mockMvc.perform(get("/auto-search")
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext)))
                    .andExpect(status().isOk());

            verify(autoSearchQueryUseCase).getUserRequests();
        }
    }
}
