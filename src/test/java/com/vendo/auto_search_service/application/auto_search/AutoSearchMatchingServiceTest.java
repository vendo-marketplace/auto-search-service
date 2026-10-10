package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.ProductDataBuilder;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.domain.user.User;
import com.vendo.auto_search_service.domain.user.UserDataBuilder;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.search.SearchPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutoSearchMatchingServiceTest {

    @InjectMocks
    private AutoSearchMatchingService service;

    @Mock
    private SearchPort searchPort;
    @Mock
    private AutoSearchQueryPort autoSearchQueryPort;
    @Mock
    private AutoSearchCommandPort autoSearchCommandPort;
    @Mock
    private AutoSearchEventSenderPort eventSenderPort;

    @Test
    void match_shouldMatchInitProductsByRequest() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        User user = UserDataBuilder.withAllFields().build();
        Product product = ProductDataBuilder.withAllFields().build();

        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(List.of(product)));
        service.matchInit(autoSearch.id(), user.email());

        ArgumentCaptor<SearchRequestCommand> searchCaptor = ArgumentCaptor.forClass(SearchRequestCommand.class);
        ArgumentCaptor<Set<String>> productIdsCaptor = ArgumentCaptor.forClass(Set.class);

        verify(autoSearchQueryPort).findById(autoSearch.id());
        verify(searchPort).search(searchCaptor.capture());
        verify(autoSearchCommandPort).addProducts(eq(autoSearch.id()), productIdsCaptor.capture());
        verify(eventSenderPort).sendRequestReady(autoSearch.id(), user.email());

        SearchRequestCommand requestCommandValue = searchCaptor.getValue();
        assertThat(requestCommandValue.categoryId()).isEqualTo(autoSearch.categoryId());
        assertThat(requestCommandValue.addressFilter()).isEqualTo(autoSearch.address());
        assertThat(requestCommandValue.priceRangeFilter()).isNotNull();
        assertThat(requestCommandValue.priceRangeFilter().minPrice()).isEqualTo(autoSearch.minPrice());
        assertThat(requestCommandValue.priceRangeFilter().maxPrice()).isEqualTo(autoSearch.maxPrice());

        assertThat(productIdsCaptor.getValue()).containsExactly(product.id());
    }

    @Test
    void match_Init_shouldNotUpdateAndSentEvent_whenNoProductsFound() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        User user = UserDataBuilder.withAllFields().build();

        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(List.of()));

        service.matchInit(autoSearch.id(), user.email());

        ArgumentCaptor<SearchRequestCommand> searchCaptor = ArgumentCaptor.forClass(SearchRequestCommand.class);

        verify(autoSearchQueryPort).findById(autoSearch.id());
        verify(searchPort).search(searchCaptor.capture());

        verifyNoInteractions(autoSearchCommandPort, eventSenderPort);

        SearchRequestCommand requestCommandValue = searchCaptor.getValue();
        assertThat(requestCommandValue.categoryId()).isEqualTo(autoSearch.categoryId());
        assertThat(requestCommandValue.addressFilter()).isEqualTo(autoSearch.address());
        assertThat(requestCommandValue.priceRangeFilter()).isNotNull();
        assertThat(requestCommandValue.priceRangeFilter().minPrice()).isEqualTo(autoSearch.minPrice());
        assertThat(requestCommandValue.priceRangeFilter().maxPrice()).isEqualTo(autoSearch.maxPrice());
    }

    @Test
    void matchInit_shouldOmitOptionalFilters_whenAutoSearchHasNoPriceOrCity() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .minPrice(null)
                .maxPrice(null)
                .address(null)
                .build();
        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(List.of()));

        service.matchInit(autoSearch.id(), "user@example.com");

        ArgumentCaptor<SearchRequestCommand> searchCaptor = ArgumentCaptor.forClass(SearchRequestCommand.class);
        verify(searchPort).search(searchCaptor.capture());
        assertThat(searchCaptor.getValue().categoryId()).isEqualTo(autoSearch.categoryId());
        assertThat(searchCaptor.getValue().addressFilter()).isNull();
        assertThat(searchCaptor.getValue().priceRangeFilter()).isNull();
        verifyNoInteractions(autoSearchCommandPort, eventSenderPort);
    }

    @Test
    void matchNew_shouldAddProductAndSendEventForMatchingAutoSearches() {
        Product product = ProductDataBuilder.withAllFields().build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(List.of(autoSearch));

        service.matchNew(product);

        verify(autoSearchQueryPort).findAll(any(), eq(org.springframework.data.domain.PageRequest.of(0, 100)));
        verify(autoSearchCommandPort).addProducts(autoSearch.id(), Set.of(product.id()));
        verify(eventSenderPort).sendRequestNewProduct(autoSearch.id(), autoSearch.owner().email());
    }

    @Test
    void matchNew_shouldFetchNextPageWhenPageIsFull() {
        Product product = ProductDataBuilder.withAllFields().build();
        List<AutoSearch> fullPage = java.util.stream.IntStream.range(0, 100)
                .mapToObj(i -> AutoSearchDataBuilder.withAllFields().id("auto-search-" + i).build())
                .toList();
        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(fullPage, List.of());

        service.matchNew(product);

        verify(autoSearchQueryPort).findAll(any(), eq(org.springframework.data.domain.PageRequest.of(0, 100)));
        verify(autoSearchQueryPort).findAll(any(), eq(org.springframework.data.domain.PageRequest.of(1, 100)));
        verify(autoSearchCommandPort, times(100)).addProducts(anyString(), eq(Set.of(product.id())));
        verify(eventSenderPort, times(100)).sendRequestNewProduct(anyString(), anyString());
    }
}
