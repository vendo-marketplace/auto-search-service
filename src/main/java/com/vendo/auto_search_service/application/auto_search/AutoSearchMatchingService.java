package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.auto_search.command.FindAllRequest;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchMatchingUseCase;
import com.vendo.auto_search_service.port.search.SearchPort;
import com.vendo.auto_search_service.shared.Address;
import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.core_lib.utils.ObjectUtils;
import com.vendo.core_lib.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AutoSearchMatchingService implements AutoSearchMatchingUseCase {

    private final SearchPort searchPort;

    private final AutoSearchQueryPort autoSearchQueryPort;
    private final AutoSearchCommandPort autoSearchCommandPort;
    private final AutoSearchEventSenderPort eventSenderPort;

    private static final int MAX_PAGE_SIZE = 100, FIRST_PAGE = 0, MAX_NOTIFIED_PRODUCTS = 5;

    @Override
    public void matchInit(String id, String email) {
        AutoSearch autoSearch = autoSearchQueryPort.findById(id);

        SearchResponseCommand response = searchPort.search(buildSearchRequest(autoSearch));
        if (CollectionUtils.isEmpty(response.data())) return;

        Set<String> productIds = Product.getProductIds(response.data());
        autoSearchCommandPort.update(id, AutoSearch.builder().products(productIds).build());

        eventSenderPort.sendRequestReady(id, email, response.data().stream().limit(MAX_NOTIFIED_PRODUCTS).toList());
        autoSearchCommandPort.addNotifiedProducts(id, productIds);
    }

    @Override
    public void matchNew(Product product) {
        int page = FIRST_PAGE;
        FindAllRequest request = FindAllRequest.from(product.categoryId(), product.address(), product.price());

        while (true) {
            List<AutoSearch> entities = autoSearchQueryPort.findAll(request, PageRequest.of(page++, MAX_PAGE_SIZE));
            entities.parallelStream().forEach(autoSearch -> processEntity(product, autoSearch));
            if (entities.size() < MAX_PAGE_SIZE) break;
        }
    }

    private void processEntity(Product product, AutoSearch autoSearch) {
        if (autoSearch.isNotified(product.id())) return;

        autoSearchCommandPort.addProducts(autoSearch.id(), Set.of(product.id()));
        eventSenderPort.sendRequestNewProduct(autoSearch.id(), autoSearch.owner().email(), List.of(product));
        autoSearchCommandPort.addNotifiedProducts(autoSearch.id(), Set.of(product.id()));
    }

    private SearchRequestCommand buildSearchRequest(AutoSearch autoSearch) {
        SearchRequestCommand.SearchRequestCommandBuilder builder = SearchRequestCommand.builder();

        if (ObjectUtils.isAnyNonNull(autoSearch.minPrice(), autoSearch.maxPrice())) {
            builder.priceRangeFilter(PriceRangeFilter.from(autoSearch.minPrice(), autoSearch.maxPrice()));
        }

        if (ObjectUtils.isNotNull(autoSearch.address()) && !StringUtils.isEmpty(autoSearch.address().city())) {
            builder.addressFilter(Address.from(autoSearch.address().city()));
        }

        return builder.categoryId(autoSearch.categoryId()).build();
    }
}
