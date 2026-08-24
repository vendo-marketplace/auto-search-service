package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.product.ProductId;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.PriceRangeFilter;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchMatchingUseCase;
import com.vendo.auto_search_service.port.search.SearchPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutoSearchMatchingService implements AutoSearchMatchingUseCase {

    private final SearchPort searchPort;
    private final AutoSearchQueryPort autoSearchQueryPort;
    private final AutoSearchCommandPort autoSearchCommandPort;

    @Override
    public void match(String autoSearchId) {
        AutoSearch autoSearch = autoSearchQueryPort.findById(autoSearchId);
        SearchResponseCommand response = searchPort.search(buildSearchRequest(autoSearch));

        AutoSearch update = AutoSearch.builder().products(ProductId.getProductIds(response.data())).build();
        autoSearchCommandPort.update(autoSearchId, update);
    }

    private SearchRequestCommand buildSearchRequest(AutoSearch autoSearch) {
        PriceRangeFilter priceRange = PriceRangeFilter.from(autoSearch.minPrice(), autoSearch.maxPrice());
        return SearchRequestCommand.from(autoSearch.categoryId(), autoSearch.address(), priceRange);
    }

}
