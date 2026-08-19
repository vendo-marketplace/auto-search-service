package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.category.Category;
import com.vendo.auto_search_service.domain.category.CategoryType;
import com.vendo.auto_search_service.infrastructure.props.ExpirationDateProps;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import com.vendo.auto_search_service.port.category.CategoryQueryPort;
import com.vendo.core_lib.utils.ObjectUtils;
import com.vendo.core_lib.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
class AutoSearchCommandService implements AutoSearchCommandUseCase {

    private final AutoSearchCommandPort commandPort;
    private final AutoSearchQueryPort queryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final AuthUserPort authUserPort;
    private final ExpirationDateProps expirationProps;

    @Override
    public void create(AutoSearch autoSearch) {
        Category category = categoryQueryPort.findById(autoSearch.categoryId());
        category.throwIfNotDesiredType(CategoryType.CHILD);

        AutoSearch toSave = autoSearch.toBuilder()
                .userId(authUserPort.getAuthUser().id())
                .status(SearchStatus.ACTIVE)
                .expirationDate(resolveExpiration(autoSearch.expirationDate()))
                .notifiedProducts(Set.of())
                .build();

        commandPort.save(toSave);
    }

    @Override
    public void update(String id, AutoSearch autoSearch) {
        AutoSearch existing = queryPort.findById(id);
        authUserPort.validateAuthOwner(existing.userId());

        validateIfChanged(autoSearch.categoryId());
        validateIfChanged(autoSearch.expirationDate());

        commandPort.update(id, autoSearch);
    }

    @Override
    public void delete(String id) {
        AutoSearch existing = queryPort.findById(id);
        authUserPort.validateAuthOwner(existing.userId());
        commandPort.delete(id);
    }

    private void validateIfChanged(String categoryId) {
        if (!StringUtils.isEmpty(categoryId)) {
            Category category = categoryQueryPort.findById(categoryId);
            category.throwIfNotDesiredType(CategoryType.CHILD);
        }
    }

    private void validateIfChanged(LocalDateTime expirationDate) {
        if (ObjectUtils.isNotNull(expirationDate)) {
            AutoSearch.validateExpirationDate(expirationDate, expirationProps.getMinDays(), expirationProps.getMaxDays());
        }
    }

    private LocalDateTime resolveExpiration(LocalDateTime expirationDate) {
        if (ObjectUtils.isNull(expirationDate)) {
            return LocalDateTime.now().plusDays(expirationProps.getMaxDays());
        }

        validateIfChanged(expirationDate);
        return expirationDate;
    }
}
