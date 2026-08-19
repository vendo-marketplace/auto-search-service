package com.vendo.auto_search_service.adapter.category.out;

import com.vendo.auto_search_service.adapter.category.out.config.ProductFeignConfig;
import com.vendo.auto_search_service.adapter.category.out.dto.CategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "product-service",
        path = "/internal/categories",
        configuration = ProductFeignConfig.class)
public interface CategoryClient {

    @GetMapping("/{id}")
    CategoryResponse findById(@PathVariable("id") String id);

}
