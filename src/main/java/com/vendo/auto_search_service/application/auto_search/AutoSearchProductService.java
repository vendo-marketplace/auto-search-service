package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchProductUseCase;
import com.vendo.auto_search_service.port.search.SearchPort;
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
        authUserPort.validateAuthOwner(autoSearch.userId());
        // TODO find products by ids
        return List.of();
    }

}
