package com.vendo.auto_search_service.adapter.auto_search.in;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.CreateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.in.dto.UpdateAutoSearchRequest;
import com.vendo.auto_search_service.adapter.auto_search.out.mapper.DtoAutoSearchMapper;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auto-search")
public class AutoSearchCommandController {

    private final DtoAutoSearchMapper mapper;
    private final AutoSearchCommandUseCase autoSearchCommandUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid CreateAutoSearchRequest request) {
        autoSearchCommandUseCase.create(mapper.toEntity(request));
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody @Valid UpdateAutoSearchRequest request) {
        autoSearchCommandUseCase.update(id, mapper.toEntity(request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        autoSearchCommandUseCase.delete(id);
    }

}
