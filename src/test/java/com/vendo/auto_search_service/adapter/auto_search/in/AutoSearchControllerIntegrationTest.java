package com.vendo.auto_search_service.adapter.auto_search.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequestDataBuilder;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequestDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchQueryUseCase;
import com.vendo.security_lib.exception.ExceptionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.vendo.auto_search_service.test_utils.SecurityContextService.initializeSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutoSearchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AutoSearchCommandUseCase autoSearchCommandUseCase;
    @MockitoBean
    private AutoSearchQueryUseCase autoSearchQueryUseCase;

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

    @Nested
    class CreateTests {

        @Test
        void create_shouldReturnCreated_whenRequestIsValid() throws Exception {
            CreateAutoSearchRequest request = CreateAutoSearchRequestDataBuilder.withAllFields().build();

            mockMvc.perform(post("/auto-search")
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());

            verify(autoSearchCommandUseCase).create(any(AutoSearch.class));
        }

        @Test
        void create_shouldReturnBadRequest_whenCategoryIdBlank() throws Exception {
            CreateAutoSearchRequest request = CreateAutoSearchRequestDataBuilder.withAllFields().categoryId(" ").build();

            String content = mockMvc.perform(post("/auto-search")
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getErrors()).containsKey("categoryId");
            verifyNoInteractions(autoSearchCommandUseCase);
        }

        @Test
        void create_shouldReturnBadRequest_whenMinPriceNegative() throws Exception {
            CreateAutoSearchRequest request = CreateAutoSearchRequestDataBuilder.withAllFields()
                    .minPrice(BigDecimal.valueOf(-1))
                    .build();

            String content = mockMvc.perform(post("/auto-search")
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getErrors()).containsKey("minPrice");
            verifyNoInteractions(autoSearchCommandUseCase);
        }

        @Test
        void create_shouldReturnBadRequest_whenMinPriceGreaterThanMaxPrice() throws Exception {
            CreateAutoSearchRequest request = CreateAutoSearchRequestDataBuilder.withAllFields()
                    .minPrice(BigDecimal.valueOf(100))
                    .maxPrice(BigDecimal.TEN)
                    .build();

            mockMvc.perform(post("/auto-search")
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(autoSearchCommandUseCase);
        }

        @Test
        void create_shouldReturnBadRequest_whenExpirationDateIsTooSoon() throws Exception {
            CreateAutoSearchRequest request = CreateAutoSearchRequestDataBuilder.withAllFields()
                    .expirationDate(LocalDateTime.now())
                    .build();

            String content = mockMvc.perform(post("/auto-search")
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getErrors()).containsKey("expirationDate");
            verifyNoInteractions(autoSearchCommandUseCase);
        }

        @Test
        void create_shouldReturnBadRequest_whenExpirationDateIsTooFar() throws Exception {
            CreateAutoSearchRequest request = CreateAutoSearchRequestDataBuilder.withAllFields()
                    .expirationDate(LocalDateTime.now().plusDays(30))
                    .build();

            String content = mockMvc.perform(post("/auto-search")
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getErrors()).containsKey("expirationDate");
            verifyNoInteractions(autoSearchCommandUseCase);
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void update_shouldReturnOk_whenRequestIsValid() throws Exception {
            String id = "auto-search-id";
            UpdateAutoSearchRequest request = UpdateAutoSearchRequestDataBuilder.withAllFields().build();

            mockMvc.perform(put("/auto-search/{id}", id)
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(autoSearchCommandUseCase).update(eq(id), any(AutoSearch.class));
        }

        @Test
        void update_shouldReturnBadRequest_whenStatusIsExpired() throws Exception {
            String id = "auto-search-id";
            UpdateAutoSearchRequest request = UpdateAutoSearchRequestDataBuilder.withAllFields()
                    .status(SearchStatus.EXPIRED)
                    .build();

            String content = mockMvc.perform(put("/auto-search/{id}", id)
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getErrors()).containsKey("status");
            verifyNoInteractions(autoSearchCommandUseCase);
        }

        @Test
        void update_shouldReturnNotFound_whenRequestDoesNotExistOrNotOwned() throws Exception {
            String id = "auto-search-id";
            UpdateAutoSearchRequest request = UpdateAutoSearchRequestDataBuilder.withAllFields().build();

            doThrow(new AutoSearchNotFoundException("Auto search request not found."))
                    .when(autoSearchCommandUseCase).update(eq(id), any(AutoSearch.class));

            String content = mockMvc.perform(put("/auto-search/{id}", id)
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Auto search request not found.");
            assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        }
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

        @Test
        void getById_shouldReturnRequest() throws Exception {
            AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();

            when(autoSearchQueryUseCase.getById(autoSearch.id())).thenReturn(autoSearch);

            mockMvc.perform(get("/auto-search/{id}", autoSearch.id())
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext)))
                    .andExpect(status().isOk());

            verify(autoSearchQueryUseCase).getById(autoSearch.id());
        }

        @Test
        void getById_shouldReturnNotFound_whenRequestDoesNotExistOrNotOwned() throws Exception {
            String id = "auto-search-id";

            when(autoSearchQueryUseCase.getById(id))
                    .thenThrow(new AutoSearchNotFoundException("Auto search request not found."));

            mockMvc.perform(get("/auto-search/{id}", id)
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void delete_shouldReturnOk() throws Exception {
            String id = "auto-search-id";

            mockMvc.perform(delete("/auto-search/{id}", id)
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext)))
                    .andExpect(status().isOk());

            verify(autoSearchCommandUseCase).delete(id);
        }

        @Test
        void delete_shouldReturnNotFound_whenRequestDoesNotExistOrNotOwned() throws Exception {
            String id = "auto-search-id";

            doThrow(new AutoSearchNotFoundException("Auto search request not found."))
                    .when(autoSearchCommandUseCase).delete(id);

            mockMvc.perform(delete("/auto-search/{id}", id)
                            .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext)))
                    .andExpect(status().isNotFound());
        }
    }
}
