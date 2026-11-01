package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.ProductDataBuilder;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.infrastructure.props.MatchingProps;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.search.SearchPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutoSearchMatchingServiceTest {

    private static final int PAGE_SIZE = 100;

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
    @Mock
    private MatchingProps matchingProps;

    @BeforeEach
    void setUp() {
        lenient().when(matchingProps.getPageSize()).thenReturn(PAGE_SIZE);
    }

    @Test
    void match_shouldMatchInitProductsByRequest() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        Product product = ProductDataBuilder.withAllFields().ownerId("another-user-id").build();

        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(List.of(product), null));
        service.matchInit(autoSearch.id(), autoSearch.owner().email());

        ArgumentCaptor<SearchRequestCommand> searchCaptor = ArgumentCaptor.forClass(SearchRequestCommand.class);
        ArgumentCaptor<Set<String>> productIdsCaptor = ArgumentCaptor.forClass(Set.class);

        verify(autoSearchQueryPort).findById(autoSearch.id());
        verify(searchPort).search(searchCaptor.capture());
        verify(autoSearchCommandPort).addProducts(eq(autoSearch.id()), productIdsCaptor.capture());
        verify(eventSenderPort).sendRequestReady(autoSearch.id(), autoSearch.owner().email());

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

        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(List.of(), null));

        service.matchInit(autoSearch.id(), autoSearch.owner().email());

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
    void matchInit_shouldExcludeProductsOwnedByAutoSearchOwner() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        Product ownProduct = ProductDataBuilder.withAllFields()
                .id("own-product")
                .ownerId(autoSearch.owner().id())
                .build();
        Product otherProduct = ProductDataBuilder.withAllFields()
                .id("other-product")
                .ownerId("another-user-id")
                .build();
        Product productWithoutOwner = ProductDataBuilder.withAllFields()
                .id("product-without-owner")
                .ownerId(null)
                .build();
        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(Arrays.asList(ownProduct, otherProduct, productWithoutOwner, null), null));

        service.matchInit(autoSearch.id(), autoSearch.owner().email());

        verify(autoSearchCommandPort).addProducts(autoSearch.id(), Set.of(otherProduct.id()));
        verify(eventSenderPort).sendRequestReady(autoSearch.id(), autoSearch.owner().email());
    }

    @Test
    void matchInit_shouldOmitOptionalFilters_whenAutoSearchHasNoPriceOrCity() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .minPrice(null)
                .maxPrice(null)
                .address(null)
                .build();
        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(List.of(), null));

        service.matchInit(autoSearch.id(), autoSearch.owner().email());

        ArgumentCaptor<SearchRequestCommand> searchCaptor = ArgumentCaptor.forClass(SearchRequestCommand.class);
        verify(searchPort).search(searchCaptor.capture());
        assertThat(searchCaptor.getValue().categoryId()).isEqualTo(autoSearch.categoryId());
        assertThat(searchCaptor.getValue().addressFilter()).isNull();
        assertThat(searchCaptor.getValue().priceRangeFilter()).isNull();
        verifyNoInteractions(autoSearchCommandPort, eventSenderPort);
    }

    @Test
    void matchNew_shouldAddProductAndSendEventForMatchingAutoSearches() {
        Product product = ProductDataBuilder.withAllFields().ownerId("product-owner-id").build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(List.of(autoSearch));

        service.matchNew(product);

        verify(autoSearchQueryPort).findAll(any(), eq(org.springframework.data.domain.PageRequest.of(0, 100)));
        verify(autoSearchCommandPort).addProducts(autoSearch.id(), Set.of(product.id()));
        verify(eventSenderPort).sendRequestNewProduct(autoSearch.id(), autoSearch.owner().email());
    }

    @Test
    void matchNew_shouldSkipAutoSearchOwnedByProductOwner() {
        Product product = ProductDataBuilder.withAllFields().ownerId("user-id").build();
        AutoSearch ownAutoSearch = AutoSearchDataBuilder.withAllFields().build();
        AutoSearch otherAutoSearch = AutoSearchDataBuilder.withAllFields()
                .id("other-auto-search")
                .owner(Owner.from("another-user-id", "other@example.com"))
                .build();
        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(List.of(ownAutoSearch, otherAutoSearch));

        service.matchNew(product);

        verify(autoSearchCommandPort).addProducts(otherAutoSearch.id(), Set.of(product.id()));
        verify(autoSearchCommandPort, never()).addProducts(eq(ownAutoSearch.id()), anySet());
        verify(eventSenderPort).sendRequestNewProduct(otherAutoSearch.id(), otherAutoSearch.owner().email());
        verify(eventSenderPort, never()).sendRequestNewProduct(eq(ownAutoSearch.id()), anyString());
    }

    @Test
    void matchNew_shouldSkipProductWithoutOwnerId() {
        Product product = ProductDataBuilder.withAllFields().ownerId(null).build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(List.of(autoSearch));

        service.matchNew(product);

        verifyNoInteractions(autoSearchCommandPort, eventSenderPort);
    }

    @Test
    void matchNew_shouldSkipProductWithBlankOwnerId() {
        Product product = ProductDataBuilder.withAllFields().ownerId("").build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(List.of(autoSearch));

        service.matchNew(product);

        verifyNoInteractions(autoSearchCommandPort, eventSenderPort);
    }

    @Test
    void matchNew_shouldSkipAutoSearchWithoutOwnerId() {
        Product product = ProductDataBuilder.withAllFields().ownerId("product-owner-id").build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .owner(Owner.from(null, "owner@example.com"))
                .build();
        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(List.of(autoSearch));

        service.matchNew(product);

        verifyNoInteractions(autoSearchCommandPort, eventSenderPort);
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
