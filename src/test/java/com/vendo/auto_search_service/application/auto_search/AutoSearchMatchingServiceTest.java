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

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
        doNothing().when(autoSearchCommandPort).update(eq(autoSearch.id()), any());

        service.matchInit(autoSearch.id(), user.email());

        ArgumentCaptor<SearchRequestCommand> searchCaptor = ArgumentCaptor.forClass(SearchRequestCommand.class);
        ArgumentCaptor<AutoSearch> autoSearchCaptor = ArgumentCaptor.forClass(AutoSearch.class);

        verify(autoSearchQueryPort).findById(autoSearch.id());
        verify(searchPort).search(searchCaptor.capture());
        verify(autoSearchCommandPort).update(eq(autoSearch.id()), autoSearchCaptor.capture());
        verify(eventSenderPort).sendRequestReady(autoSearch.id(), user.email(), List.of(product));

        SearchRequestCommand requestCommandValue = searchCaptor.getValue();
        assertThat(requestCommandValue.categoryId()).isEqualTo(autoSearch.categoryId());
        assertThat(requestCommandValue.addressFilter()).isEqualTo(autoSearch.address());
        assertThat(requestCommandValue.priceRangeFilter()).isNotNull();
        assertThat(requestCommandValue.priceRangeFilter().minPrice()).isEqualTo(autoSearch.minPrice());
        assertThat(requestCommandValue.priceRangeFilter().maxPrice()).isEqualTo(autoSearch.maxPrice());

        AutoSearch autoSearchValue = autoSearchCaptor.getValue();
        assertThat(autoSearchValue.products()).isNotNull();
        assertThat(autoSearchValue.products().size()).isEqualTo(1);
        assertThat(autoSearchValue.products().iterator().next()).isEqualTo(product.id());
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
    void matchInit_shouldSendOnlyTopProducts_whenMoreThanLimitFound() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        User user = UserDataBuilder.withAllFields().build();
        List<Product> products = Stream.generate(() -> ProductDataBuilder.withAllFields().build()).limit(7).toList();

        when(autoSearchQueryPort.findById(autoSearch.id())).thenReturn(autoSearch);
        when(searchPort.search(any())).thenReturn(new SearchResponseCommand(products));

        service.matchInit(autoSearch.id(), user.email());

        ArgumentCaptor<AutoSearch> autoSearchCaptor = ArgumentCaptor.forClass(AutoSearch.class);
        verify(autoSearchCommandPort).update(eq(autoSearch.id()), autoSearchCaptor.capture());
        verify(eventSenderPort).sendRequestReady(autoSearch.id(), user.email(), products.subList(0, 5));

        assertThat(autoSearchCaptor.getValue().products().size()).isEqualTo(7);
    }

    @Test
    void matchNew_shouldSendNewProduct_forEachMatchingRequest() {
        Product product = ProductDataBuilder.withAllFields().build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().products(new HashSet<>()).build();

        when(autoSearchQueryPort.findAll(any(), any())).thenReturn(List.of(autoSearch));

        service.matchNew(product);

        verify(autoSearchCommandPort).update(eq(autoSearch.id()), any(AutoSearch.class));
        verify(eventSenderPort).sendRequestNewProduct(autoSearch.id(), autoSearch.owner().email(), List.of(product));
    }
}
