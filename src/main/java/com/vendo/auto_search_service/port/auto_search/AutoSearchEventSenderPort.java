package com.vendo.auto_search_service.port.auto_search;

import com.vendo.auto_search_service.domain.product.Product;

import java.util.List;

public interface AutoSearchEventSenderPort {

    void sendMatching(String id, String email);

    void sendRequestReady(String id, String email, List<Product> products);

    void sendRequestNewProduct(String id, String email, List<Product> products);

}
