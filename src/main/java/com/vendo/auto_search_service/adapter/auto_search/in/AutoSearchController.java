package com.vendo.auto_search_service.adapter.auto_search.in;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchResponse;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.out.mapper.DtoAutoSearchMapper;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchQueryUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auto-search")
@RequiredArgsConstructor
public class AutoSearchController {

    private final AutoSearchCommandUseCase autoSearchCommandUseCase;
    private final AutoSearchQueryUseCase autoSearchQueryUseCase;
    private final DtoAutoSearchMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid CreateAutoSearchRequest request) {
        autoSearchCommandUseCase.create(mapper.toEntity(request));
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody @Valid UpdateAutoSearchRequest request) {
        autoSearchCommandUseCase.update(id, mapper.toEntity(request));
    }

    @GetMapping
    public List<AutoSearchResponse> getUserRequests() {
        return mapper.toResponses(autoSearchQueryUseCase.getUserRequests());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        autoSearchCommandUseCase.delete(id);
    }
}
