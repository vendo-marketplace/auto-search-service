package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.adapter.auto_search.out.mapper.AutoSearchMapper;
import com.vendo.auto_search_service.application.auto_search.command.AutoSearchDataCommand;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSearchQueryAdapterTest {

    @InjectMocks
    private AutoSearchQueryAdapter queryAdapter;

    @Mock
    private AutoSearchMapper mapper;
    @Mock
    private MongoAutoSearchRepository repository;

    @Test
    void findByUserId_shouldReturnMappedRequests() {
        String userId = "user-id";
        MongoAutoSearch entity = MongoAutoSearch.builder().id("id").userId(userId).build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().id("id").userId(userId).build();

        when(repository.findAllByUserId(userId)).thenReturn(List.of(entity));
        when(mapper.toAutoSearches(List.of(entity))).thenReturn(List.of(autoSearch));

        assertThat(queryAdapter.findByUserId(userId)).containsExactly(autoSearch);
    }

    @Test
    void findById_shouldReturnMappedRequest_whenFound() {
        MongoAutoSearch entity = MongoAutoSearch.builder().id("id").build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().id("id").build();

        when(repository.findById("id")).thenReturn(Optional.of(entity));
        when(mapper.toAutoSearch(entity)).thenReturn(autoSearch);

        assertThat(queryAdapter.findById("id")).isEqualTo(autoSearch);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(repository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryAdapter.findById("missing-id"))
                .isInstanceOf(AutoSearchNotFoundException.class);
    }

    @Test
    void findOutdatedActiveRequests_shouldQueryByActiveStatusAndReferenceTime_andMapResult() {
        LocalDateTime referenceTime = LocalDateTime.now();
        MongoAutoSearch entity = MongoAutoSearch.builder().id("outdated-id").status(SearchStatus.ACTIVE).build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().id("outdated-id").build();

        when(repository.findAllByStatusAndExpirationDateBefore(eq(SearchStatus.ACTIVE), eq(referenceTime), any()))
                .thenReturn(new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 15));
        when(mapper.toAutoSearches(List.of(entity))).thenReturn(List.of(autoSearch));

        AutoSearchDataCommand result = queryAdapter.findOutdatedActiveRequests(referenceTime, 0, 10);

        assertThat(result.data()).containsExactly(autoSearch);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    void findOutdatedActiveRequests_shouldPassGivenPageAndSize_toRepository() {
        LocalDateTime referenceTime = LocalDateTime.now();

        when(repository.findAllByStatusAndExpirationDateBefore(eq(SearchStatus.ACTIVE), eq(referenceTime), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(mapper.toAutoSearches(List.of())).thenReturn(List.of());

        queryAdapter.findOutdatedActiveRequests(referenceTime, 2, 5);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAllByStatusAndExpirationDateBefore(eq(SearchStatus.ACTIVE), eq(referenceTime), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5);
    }
}
