package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.auto_search.command.ProductPageRequest;
import com.vendo.auto_search_service.application.search.command.SearchMetadata;
import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.infrastructure.props.ProductPageProps;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchProductUseCase;
import com.vendo.auto_search_service.port.search.SearchPort;
import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.core_lib.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class AutoSearchProductService implements AutoSearchProductUseCase {

    private final SearchPort searchPort;
    private final AuthUserPort authUserPort;
    private final ProductPageProps productPageProps;

    private final AutoSearchQueryPort autoSearchQueryPort;

    @Override
    public SearchResponseCommand findAll(String id, ProductPageRequest pageRequest) {
        AutoSearch autoSearch = autoSearchQueryPort.findById(id);
        authUserPort.validateAuthOwner(autoSearch.owner().id());

        int page = ObjectUtils.isNotNull(pageRequest.page()) ? pageRequest.page() : productPageProps.getPage();
        int size = ObjectUtils.isNotNull(pageRequest.size()) ? pageRequest.size() : productPageProps.getSize();

        if (CollectionUtils.isEmpty(autoSearch.products())) {
            return new SearchResponseCommand(List.of(), SearchMetadata.empty(page, size));
        }

        SearchRequestCommand request = SearchRequestCommand.withFilters(autoSearch)
                .ids(autoSearch.products())
                .page(page)
                .size(size)
                .sort(pageRequest.sort())
                .build();

        return searchPort.search(request);
    }

}
