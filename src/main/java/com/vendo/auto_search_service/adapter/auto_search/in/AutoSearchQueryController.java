package com.vendo.auto_search_service.adapter.auto_search.in;

import com.vendo.auto_search_service.adapter.auto_search.in.dto.AutoSearchResponse;
import com.vendo.auto_search_service.adapter.auto_search.out.mapper.DtoAutoSearchMapper;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auto-search")
@RequiredArgsConstructor
public class AutoSearchQueryController {

    private final DtoAutoSearchMapper mapper;
    private final AutoSearchQueryUseCase autoSearchQueryUseCase;

    @GetMapping
    public List<AutoSearchResponse> getUserRequests() {
        return mapper.toResponses(autoSearchQueryUseCase.getUserRequests());
    }

}
