package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.auto_search.command.FindAllRequest;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.infrastructure.props.MatchingProps;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchEventSenderPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchMatchingUseCase;
import com.vendo.auto_search_service.port.search.SearchPort;
import com.vendo.core_lib.utils.CollectionUtils;
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

    private final MatchingProps matchingProps;

    private static final int FIRST_PAGE = 0;

    @Override
    public void matchInit(String id, String email) {
        AutoSearch autoSearch = autoSearchQueryPort.findById(id);

        SearchResponseCommand response = searchPort.search(SearchRequestCommand.withFilters(autoSearch).build());
        if (CollectionUtils.isEmpty(response.data())) return;

        Set<String> productIds = Product.getProductIds(response.data());
        autoSearchCommandPort.addProducts(id, productIds);

        eventSenderPort.sendRequestReady(id, email);
    }

    @Override
    public void matchNew(Product product) {
        int page = FIRST_PAGE;
        FindAllRequest request = FindAllRequest.from(product.categoryId(), product.address(), product.price());

        int pageSize = matchingProps.getPageSize();

        while (true) {
            List<AutoSearch> entities = autoSearchQueryPort.findAll(request, PageRequest.of(page++, pageSize));
            entities.parallelStream().forEach(autoSearch -> processEntity(product.id(), autoSearch));

            if (entities.size() < pageSize) {
                break;
            }
        }
    }

    private void processEntity(String productId, AutoSearch autoSearch) {
        autoSearchCommandPort.addProducts(autoSearch.id(), Set.of(productId));
        eventSenderPort.sendRequestNewProduct(autoSearch.id(), autoSearch.owner().email());
    }
}
