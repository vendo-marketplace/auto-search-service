package com.vendo.auto_search_service.adapter.auto_search.in;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequest;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auto-search")
@RequiredArgsConstructor
public class AutoSearchController {

    private final AutoSearchCommandUseCase autoSearchCommandUseCase;

    @PostMapping
    public void create(@RequestBody @Valid CreateAutoSearchRequest request) {

    }

    @PutMapping
    public void update(@RequestBody @Valid UpdateAutoSearchRequest request) {

    }

    @DeleteMapping
    public void delete(@RequestParam String id) {
        autoSearchCommandUseCase.delete(id);
    }
}
