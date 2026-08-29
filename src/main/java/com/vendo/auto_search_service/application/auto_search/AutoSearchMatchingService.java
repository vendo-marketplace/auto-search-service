package com.vendo.auto_search_service.application.auto_search;

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
import com.vendo.core_lib.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoSearchMatchingService implements AutoSearchMatchingUseCase {

    private final SearchPort searchPort;

    private final AutoSearchQueryPort autoSearchQueryPort;
    private final AutoSearchCommandPort autoSearchCommandPort;
    private final AutoSearchEventSenderPort eventSenderPort;

    @Override
    public void match(String id, String email) {
        AutoSearch autoSearch = autoSearchQueryPort.findById(id);

        SearchResponseCommand response = searchPort.search(buildSearchRequest(autoSearch));
        if (CollectionUtils.isEmpty(response.data())) {
            return;
        }

        AutoSearch update = AutoSearch.builder().products(Product.getProductIds(response.data())).build();
        autoSearchCommandPort.update(id, update);

        eventSenderPort.sendRequestReady(id, email);
    }

    private SearchRequestCommand buildSearchRequest(AutoSearch autoSearch) {
        PriceRangeFilter priceRange = PriceRangeFilter.from(autoSearch.minPrice(), autoSearch.maxPrice());
        return SearchRequestCommand.builder()
                .categoryId(autoSearch.categoryId())
                .address(autoSearch.address())
                .priceRangeFilter(priceRange)
                .build();
    }

}
