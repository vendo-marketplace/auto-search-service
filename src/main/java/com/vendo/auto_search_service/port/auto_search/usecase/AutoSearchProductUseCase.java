package com.vendo.auto_search_service.port.auto_search.usecase;

import com.vendo.auto_search_service.domain.product.Product;

import java.util.List;

public interface AutoSearchProductUseCase {

    List<Product> findAll(String autoSearchId);

}
