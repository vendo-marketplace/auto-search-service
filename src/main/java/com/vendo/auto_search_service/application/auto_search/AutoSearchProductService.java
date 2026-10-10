package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.application.search.command.SearchRequestCommand;
import com.vendo.auto_search_service.application.search.command.SearchResponseCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchProductUseCase;
import com.vendo.auto_search_service.port.search.SearchPort;
import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.core_lib.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class AutoSearchProductService implements AutoSearchProductUseCase {

    private final SearchPort searchPort;
    private final AuthUserPort authUserPort;

    private final AutoSearchQueryPort autoSearchQueryPort;
    private final AutoSearchCommandPort autoSearchCommandPort;

    @Override
    public List<Product> findAll(String id) {
        AutoSearch autoSearch = autoSearchQueryPort.findById(id);
        List<Product> products = search(autoSearch);

        List<Product> filteredProducts = filterIrrelevantProducts(autoSearch, products);
        updateWithRelevantProducts(autoSearch, filteredProducts);

        return filteredProducts;
    }

    private List<Product> search(AutoSearch autoSearch) {
        authUserPort.validateAuthOwner(autoSearch.owner().id());

        if (CollectionUtils.isEmpty(autoSearch.products())) {
            return List.of();
        }

        SearchRequestCommand requestCommand = SearchRequestCommand.builder().ids(autoSearch.products()).build();
        SearchResponseCommand command = searchPort.search(requestCommand);

        return command.data();
    }

    private List<Product> filterIrrelevantProducts(AutoSearch autoSearch, List<Product> products) {
        return products.stream()
                .filter(product -> isMatching(autoSearch, product))
                .toList();
    }
    private void updateWithRelevantProducts(AutoSearch autoSearch, List<Product> products) {
        if (products.size() < autoSearch.products().size()) {
            Set<String> ids = products.stream().map(Product::id).collect(Collectors.toSet());
            autoSearchCommandPort.addProducts(autoSearch.id(), ids);
        }
    }

    private boolean isMatching(AutoSearch autoSearch, Product product) {
        if (!autoSearch.categoryId().equals(product.categoryId())) {
            return false;
        }

        if (ObjectUtils.isNotNull(autoSearch.address()) && autoSearch.address().city().equals(product.address().city())) {
            return false;
        }

        if (ObjectUtils.isNotNull(autoSearch.minPrice()) && autoSearch.minPrice().compareTo(product.price()) > 0) {
            return false;
        }

        return ObjectUtils.isNull(autoSearch.maxPrice()) || autoSearch.maxPrice().compareTo(product.price()) >= 1;
    }
}
