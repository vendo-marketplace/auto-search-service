package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchProductUseCase;
import com.vendo.auto_search_service.port.search.SearchPort;
import com.vendo.core_lib.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class AutoSearchProductService implements AutoSearchProductUseCase {

    private final SearchPort searchPort;
    private final AuthUserPort authUserPort;

    private final AutoSearchQueryPort autoSearchQueryPort;

    @Override
    public List<Product> findAll(String id) {
        AutoSearch autoSearch = autoSearchQueryPort.findById(id);
        return search(autoSearch);
    }

    private List<Product> search(AutoSearch autoSearch) {
        authUserPort.validateAuthOwner(autoSearch.userId());

        if (CollectionUtils.isEmpty(autoSearch.products())) {
            return List.of();
        }

        SearchRequestCommand requestCommand = SearchRequestCommand.builder().ids(autoSearch.products()).build();
        SearchResponseCommand command = searchPort.search(requestCommand);

        return command.data();
    }

}
