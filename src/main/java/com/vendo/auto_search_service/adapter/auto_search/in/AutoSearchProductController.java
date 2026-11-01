package com.vendo.auto_search_service.adapter.auto_search.in;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchProductsRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchProductsResponse;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auto-search")
@RequiredArgsConstructor
public class AutoSearchProductController {

    private final AutoSearchProductUseCase useCase;

    @GetMapping("/{id}/products")
    public ResponseEntity<AutoSearchProductsResponse> findAll(
            @PathVariable String id,
            @Valid @ModelAttribute AutoSearchProductsRequest request
    ) {
        return ResponseEntity.ok(AutoSearchProductsResponse.from(useCase.findAll(id, request.toPageRequest())));
    }

}
