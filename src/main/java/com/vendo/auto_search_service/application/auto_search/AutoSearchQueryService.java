package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchValidationPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class AutoSearchQueryService implements AutoSearchQueryUseCase {

    private final AutoSearchQueryPort queryPort;
    private final AutoSearchValidationPort validationPort;
    private final AuthUserPort authUserPort;

    @Override
    public List<AutoSearch> getUserRequests() {
        return queryPort.findByUserId(authUserPort.getAuthUser().id());
    }

    @Override
    public AutoSearch getById(String id) {
        AutoSearch autoSearch = queryPort.findById(id);
        validationPort.validateOwner(autoSearch);
        return autoSearch;
    }
}
