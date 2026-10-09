package com.vendo.auto_search_service.adapter.auto_search.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequestDataBuilder;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateSearchStatus;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import com.vendo.auto_search_service.domain.auto_search.type.SearchStatus;
import com.vendo.auto_search_service.domain.category.Category;
import com.vendo.auto_search_service.domain.category.CategoryType;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.category.CategoryQueryPort;
import com.vendo.security_lib.exception.ExceptionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.vendo.auto_search_service.test_utils.SecurityContextService.initializeSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EmbeddedKafka
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutoSearchActiveRequestsLimitIntegrationTest {

    private static final long MAX_ACTIVE_REQUESTS = 3;
    private static final String LIMIT_MESSAGE = "You can have at most 3 active auto search requests.";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutoSearchQueryPort queryPort;
    @MockitoBean
    private AutoSearchCommandPort commandPort;
    @MockitoBean
    private CategoryQueryPort categoryQueryPort;
    @MockitoBean
    private AutoSearchEventSenderPort eventSenderPort;

    private User authUser;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        authUser = UserDataBuilder.withAllFields().build();
        securityContext = initializeSecurityContext(authUser);

        when(categoryQueryPort.findById(anyString()))
                .thenReturn(Category.builder().id("category-id").type(CategoryType.CHILD).build());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_shouldReturnConflict_whenActiveRequestsLimitReached() throws Exception {
        CreateAutoSearchRequest request = CreateAutoSearchRequestDataBuilder.withAllFields().build();
        when(queryPort.countActiveByUserId(authUser.id())).thenReturn(MAX_ACTIVE_REQUESTS);

        String content = mockMvc.perform(post("/auto-search")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo(LIMIT_MESSAGE);
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/auto-search");

        verify(queryPort).countActiveByUserId(authUser.id());
        verify(commandPort, never()).save(any());
        verifyNoInteractions(eventSenderPort);
    }

    @Test
    void update_shouldReturnConflict_whenReactivatingAndActiveRequestsLimitReached() throws Exception {
        AutoSearch existing = AutoSearchDataBuilder.withAllFields()
                .owner(Owner.from(authUser.id(), authUser.email()))
                .status(SearchStatus.CANCELLED)
                .build();
        UpdateAutoSearchRequest request = UpdateAutoSearchRequest.builder().status(UpdateSearchStatus.ACTIVE).build();

        when(queryPort.findById(existing.id())).thenReturn(existing);
        when(queryPort.countActiveByUserId(authUser.id())).thenReturn(MAX_ACTIVE_REQUESTS);

        String content = mockMvc.perform(put("/auto-search/" + existing.id())
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        assertThat(exceptionResponse.getMessage()).isEqualTo(LIMIT_MESSAGE);
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.CONFLICT.value());

        verify(commandPort, never()).update(anyString(), any());
        verifyNoInteractions(eventSenderPort);
    }

}
