package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.adapter.auto_search.out.mapper.AutoSearchMapper;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
}
