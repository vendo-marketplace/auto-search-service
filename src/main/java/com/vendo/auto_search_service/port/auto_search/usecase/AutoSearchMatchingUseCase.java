package com.vendo.auto_search_service.port.auto_search.usecase;

import com.vendo.auto_search_service.domain.product.Product;

public interface AutoSearchMatchingUseCase {

    void matchInit(String id, String email);

    void matchNew(Product product);

}
