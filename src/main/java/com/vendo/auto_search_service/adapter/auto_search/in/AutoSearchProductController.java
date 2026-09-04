package com.vendo.auto_search_service.adapter.auto_search.in;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchProductsResponse;
import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auto-search")
@RequiredArgsConstructor
public class AutoSearchProductController {

    private final AutoSearchProductUseCase useCase;

    @GetMapping("/{id}/products")
    public ResponseEntity<AutoSearchProductsResponse> findAll(@PathVariable String id) {
        List<Product> products = useCase.findAll(id);
        return ResponseEntity.ok(AutoSearchProductsResponse.from(products));
    }

}
