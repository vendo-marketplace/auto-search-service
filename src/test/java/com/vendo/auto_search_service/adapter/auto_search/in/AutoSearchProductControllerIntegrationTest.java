package com.vendo.auto_search_service.adapter.auto_search.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchProductsResponse;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.ProductDataBuilder;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.domain.user.exception.UserNotOwnerException;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.search.SearchPort;
import com.vendo.security_lib.exception.ExceptionResponse;
import org.assertj.core.api.Assertions;
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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static com.vendo.auto_search_service.test_utils.SecurityContextService.initializeSecurityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EmbeddedKafka
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AutoSearchProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SearchPort searchPort;
    @MockitoBean
    private AutoSearchQueryPort autoSearchQueryPort;

    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        User authUser = UserDataBuilder.withAllFields().build();
        securityContext = initializeSecurityContext(authUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findAll_shouldReturnAutoSearchProducts() throws Exception {
        String autoSearchId = "id";
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        addAuthUser(autoSearch.userId());
        Product product = ProductDataBuilder.withAllFields();
        SearchRequestCommand requestSearch = SearchRequestCommand.builder().ids(autoSearch.products()).build();
        SearchResponseCommand responseSearch = new SearchResponseCommand(List.of(product));

        when(autoSearchQueryPort.findById(autoSearchId)).thenReturn(autoSearch);
        when(searchPort.search(requestSearch)).thenReturn(responseSearch);

        String content = mockMvc.perform(get("/auto-search/{id}/products", autoSearchId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        AutoSearchProductsResponse response = objectMapper.readValue(content, AutoSearchProductsResponse.class);
        assertThat(response).isNotNull();
        assertThat(response.data()).isNotNull();
        assertThat(response.data().size()).isEqualTo(1);
        assertThat(response.data().get(0)).isEqualTo(product);

        verify(autoSearchQueryPort).findById(autoSearchId);
        verify(searchPort).search(requestSearch);
    }

    @Test
    void findAll_shouldReturnNotFound_whenAutoSearchNotFound() throws Exception {
        String autoSearchId = "id";
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        addAuthUser(autoSearch.userId());

        when(autoSearchQueryPort.findById(autoSearchId)).thenThrow(new AutoSearchNotFoundException("Auto search request not found."));

        String content = mockMvc.perform(get("/auto-search/{id}/products", autoSearchId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn().getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        Assertions.assertThat(exceptionResponse.getMessage()).isEqualTo("Auto search request not found.");
        Assertions.assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        Assertions.assertThat(exceptionResponse.getPath()).isEqualTo("/auto-search/" + autoSearchId + "/products");

        verify(autoSearchQueryPort).findById(autoSearchId);
        verifyNoInteractions(searchPort);
    }

    @Test
    void findAll_shouldReturnForbidden_whenNotAutoSearchOwner() throws Exception {
        String autoSearchId = "id";
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        addAuthUser(autoSearch.userId());

        when(autoSearchQueryPort.findById(autoSearchId)).thenThrow(new UserNotOwnerException("You're not owner."));

        String content = mockMvc.perform(get("/auto-search/{id}/products", autoSearchId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()).andReturn().getResponse().getContentAsString();

        ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
        Assertions.assertThat(exceptionResponse.getMessage()).isEqualTo("You're not owner.");
        Assertions.assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        Assertions.assertThat(exceptionResponse.getPath()).isEqualTo("/auto-search/" + autoSearchId + "/products");

        verify(autoSearchQueryPort).findById(autoSearchId);
        verifyNoInteractions(searchPort);
    }

    private void addAuthUser(String id) {
        User authUser = UserDataBuilder.withAllFields().id(id).build();
        securityContext = initializeSecurityContext(authUser);
    }

}
