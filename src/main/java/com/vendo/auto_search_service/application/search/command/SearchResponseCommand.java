package com.vendo.auto_search_service.application.search.command;

import com.vendo.auto_search_service.domain.product.Product;

import java.util.List;

public record SearchResponseCommand(List<Product> data) {
}
