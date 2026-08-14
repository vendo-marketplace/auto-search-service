package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchValidationPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
class AutoSearchCommandService implements AutoSearchCommandUseCase {

    private final AutoSearchCommandPort commandPort;
    private final AutoSearchQueryPort queryPort;
    private final AutoSearchValidationPort validationPort;
    private final AuthUserPort authUserPort;

    @Override
    public void create(AutoSearch autoSearch) {
        validationPort.validateCategoryExists(autoSearch.categoryId());

        AutoSearch toSave = new AutoSearch(
                null,
                authUserPort.getAuthUser().id(),
                autoSearch.categoryId(),
                autoSearch.minPrice(),
                autoSearch.maxPrice(),
                autoSearch.address(),
                SearchStatus.ACTIVE,
                autoSearch.expirationDate(),
                Set.of(),
                null,
                null
        );

        commandPort.save(toSave);
    }

    @Override
    public void update(String id, AutoSearch autoSearch) {
        AutoSearch existing = queryPort.findById(id);
        validationPort.validateOwner(existing);

        if (autoSearch.categoryId() != null) {
            validationPort.validateCategoryExists(autoSearch.categoryId());
        }

        commandPort.update(id, autoSearch);
    }

    @Override
    public void delete(String id) {
        AutoSearch existing = queryPort.findById(id);
        validationPort.validateOwner(existing);

        AutoSearch cancelled = new AutoSearch(
                null, null, null, null, null, null,
                SearchStatus.CANCELLED,
                null, null, null, null
        );

        commandPort.update(id, cancelled);
    }
}
