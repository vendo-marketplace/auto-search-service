package com.vendo.auto_search_service.application.auto_search;

import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.auto_search.exception.InvalidExpirationDateException;
import com.vendo.auto_search_service.infrastructure.props.ExpirationDateProps;
import com.vendo.auto_search_service.port.auth.AuthUserPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchCommandPort;
import com.vendo.auto_search_service.port.auto_search.AutoSearchQueryPort;
import com.vendo.auto_search_service.port.auto_search.usecase.AutoSearchCommandUseCase;
import com.vendo.auto_search_service.port.category.CategoryValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
class AutoSearchCommandService implements AutoSearchCommandUseCase {

    private final AutoSearchCommandPort commandPort;
    private final AutoSearchQueryPort queryPort;
    private final CategoryValidationPort categoryValidationPort;
    private final AuthUserPort authUserPort;
    private final ExpirationDateProps expirationDateProps;

    @Override
    public void create(AutoSearch autoSearch) {
        categoryValidationPort.validateCategoryExists(autoSearch.categoryId());

        AutoSearch toSave = autoSearch.toBuilder()
                .userId(authUserPort.getAuthUser().id())
                .status(SearchStatus.ACTIVE)
                .expirationDate(resolveExpirationDate(autoSearch.expirationDate()))
                .notifiedProducts(Set.of())
                .build();

        commandPort.save(toSave);
    }

    @Override
    public void update(String id, AutoSearch autoSearch) {
        AutoSearch existing = queryPort.findById(id);
        validateOwner(existing);
        validateCategoryIfChanged(autoSearch.categoryId());
        validateExpirationDateIfChanged(autoSearch.expirationDate());

        commandPort.update(id, autoSearch);
    }

    @Override
    public void delete(String id) {
        AutoSearch existing = queryPort.findById(id);
        validateOwner(existing);

        commandPort.delete(id);
    }

    private void validateOwner(AutoSearch autoSearch) {
        String userId = authUserPort.getAuthUser().id();
        if (!autoSearch.userId().equals(userId)) {
            throw new AutoSearchNotFoundException("Auto search request not found.");
        }
    }

    private void validateCategoryIfChanged(String categoryId) {
        if (categoryId != null) {
            categoryValidationPort.validateCategoryExists(categoryId);
        }
    }

    private void validateExpirationDateIfChanged(LocalDateTime expirationDate) {
        if (expirationDate != null) {
            validateExpirationDate(expirationDate);
        }
    }

    private LocalDateTime resolveExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate == null) {
            return LocalDateTime.now().plusDays(expirationDateProps.getMaxDays());
        }

        validateExpirationDate(expirationDate);
        return expirationDate;
    }

    private void validateExpirationDate(LocalDateTime expirationDate) {
        LocalDate today = LocalDate.now();
        LocalDate date = expirationDate.toLocalDate();
        LocalDate earliest = today.plusDays(expirationDateProps.getMinDays());
        LocalDate latest = today.plusDays(expirationDateProps.getMaxDays());

        if (date.isBefore(earliest) || date.isAfter(latest)) {
            throw new InvalidExpirationDateException(
                    "Expiration date must be at least a day after today and not later than a week from now.");
        }
    }
}
