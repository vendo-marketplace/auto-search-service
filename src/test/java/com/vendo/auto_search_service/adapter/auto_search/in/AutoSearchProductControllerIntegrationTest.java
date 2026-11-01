package com.vendo.auto_search_service.adapter.auto_search.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchProductsResponse;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.ProductDataBuilder;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;
import com.vendo.auto_search_service.application.search.command.ProductSortField;
import com.vendo.auto_search_service.application.search.command.SearchMetadata;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.application.search.command.SortCommand;
import com.vendo.auto_search_service.application.search.command.SortDirection;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.search.SearchPort;
import com.vendo.auto_search_service.shared.Address;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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
    @MockitoBean
    private AutoSearchCommandPort autoSearchCommandPort;

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
    void findAll_shouldReturnAutoSearchProducts_withDefaultPage() throws Exception {
        String autoSearchId = "id";
        Product product = ProductDataBuilder.withAllFields().build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .products(Set.of(product.id()))
                .minPrice(null)
                .maxPrice(null)
                .address(null)
                .categoryId(product.categoryId())
                .build();
        addAuthUser(autoSearch.owner().id());
        SearchRequestCommand requestSearch = SearchRequestCommand.builder()
                .categoryId(autoSearch.categoryId())
                .ids(autoSearch.products())
                .page(1)
                .size(25)
                .build();
        SearchMetadata metadata = new SearchMetadata(1, 25, 1, 1, false, false);
        SearchResponseCommand responseSearch = new SearchResponseCommand(List.of(product), metadata);

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
        assertThat(response.metadata()).isEqualTo(metadata);

        verify(autoSearchQueryPort).findById(autoSearchId);
        verify(searchPort).search(requestSearch);
    }

    @Test
    void findAll_shouldSearchWithRequestedPageAndSort() throws Exception {
        String autoSearchId = "id";
        Product product = ProductDataBuilder.withAllFields().build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .products(Set.of(product.id()))
                .minPrice(null)
                .maxPrice(null)
                .address(null)
                .categoryId(product.categoryId())
                .build();
        addAuthUser(autoSearch.owner().id());
        SearchRequestCommand requestSearch = SearchRequestCommand.builder()
                .categoryId(autoSearch.categoryId())
                .ids(autoSearch.products())
                .page(2)
                .size(5)
                .sort(new SortCommand(ProductSortField.PRICE, SortDirection.ASC))
                .build();
        SearchMetadata metadata = new SearchMetadata(2, 5, 2, 6, true, false);

        when(autoSearchQueryPort.findById(autoSearchId)).thenReturn(autoSearch);
        when(searchPort.search(requestSearch)).thenReturn(new SearchResponseCommand(List.of(product), metadata));

        String content = mockMvc.perform(get("/auto-search/{id}/products", autoSearchId)
                        .param("page", "2")
                        .param("size", "5")
                        .param("sortBy", "PRICE")
                        .param("direction", "ASC")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        AutoSearchProductsResponse response = objectMapper.readValue(content, AutoSearchProductsResponse.class);
        assertThat(response.metadata()).isEqualTo(metadata);

        verify(autoSearchQueryPort).findById(autoSearchId);
        verify(searchPort).search(requestSearch);
    }

    @Test
    void findAll_shouldSearchWithAutoSearchFilters() throws Exception {
        String autoSearchId = "id";
        Product product = ProductDataBuilder.withAllFields().build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .products(Set.of(product.id()))
                .minPrice(BigDecimal.TEN)
                .maxPrice(BigDecimal.valueOf(100))
                .address(product.address())
                .categoryId(product.categoryId())
                .build();
        addAuthUser(autoSearch.owner().id());
        SearchRequestCommand requestSearch = SearchRequestCommand.builder()
                .categoryId(autoSearch.categoryId())
                .priceRangeFilter(PriceRangeFilter.from(BigDecimal.TEN, BigDecimal.valueOf(100)))
                .addressFilter(Address.from(product.address().city()))
                .ids(autoSearch.products())
                .page(1)
                .size(25)
                .build();

        when(autoSearchQueryPort.findById(autoSearchId)).thenReturn(autoSearch);
        when(searchPort.search(requestSearch)).thenReturn(new SearchResponseCommand(List.of(), SearchMetadata.empty(1, 25)));

        mockMvc.perform(get("/auto-search/{id}/products", autoSearchId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(autoSearchQueryPort).findById(autoSearchId);
        verify(searchPort).search(requestSearch);
    }

    @Test
    void findAll_shouldReturnEmptyPage_whenAutoSearchHasNoProducts() throws Exception {
        String autoSearchId = "id";
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().products(Set.of()).build();
        addAuthUser(autoSearch.owner().id());

        when(autoSearchQueryPort.findById(autoSearchId)).thenReturn(autoSearch);

        String content = mockMvc.perform(get("/auto-search/{id}/products", autoSearchId)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        AutoSearchProductsResponse response = objectMapper.readValue(content, AutoSearchProductsResponse.class);
        assertThat(response.data()).isEqualTo(List.of());
        assertThat(response.metadata()).isEqualTo(SearchMetadata.empty(1, 25));

        verify(autoSearchQueryPort).findById(autoSearchId);
        verifyNoInteractions(searchPort);
    }

    @Test
    void findAll_shouldReturnBadRequest_whenPageIsLessThanOne() throws Exception {
        String autoSearchId = "id";

        mockMvc.perform(get("/auto-search/{id}/products", autoSearchId)
                        .param("page", "0")
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(securityContext))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(autoSearchQueryPort, searchPort);
    }

    @Test
    void findAll_shouldReturnNotFound_whenAutoSearchNotFound() throws Exception {
        String autoSearchId = "id";
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        addAuthUser(autoSearch.owner().id());

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
        addAuthUser("another-user-id");

        when(autoSearchQueryPort.findById(autoSearchId)).thenReturn(autoSearch);

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
