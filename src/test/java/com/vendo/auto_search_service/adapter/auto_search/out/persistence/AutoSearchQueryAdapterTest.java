package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.adapter.auto_search.out.mapper.AutoSearchMapper;
import com.vendo.auto_search_service.domain.auto_search.AutoSearch;
import com.vendo.auto_search_service.domain.auto_search.AutoSearchDataBuilder;
import com.vendo.auto_search_service.domain.auto_search.exception.AutoSearchNotFoundException;
import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        Owner owner = Owner.from(userId, "user@example.com");
        MongoAutoSearch entity = MongoAutoSearch.builder().id("id").owner(owner).build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().id("id").owner(owner).build();

        when(repository.findAllByOwner_Id(userId)).thenReturn(List.of(entity));
        when(mapper.toAutoSearches(List.of(entity))).thenReturn(List.of(autoSearch));

        assertThat(queryAdapter.findByUserId(userId)).containsExactly(autoSearch);

        verify(repository).findAllByOwner_Id(userId);
        verify(mapper).toAutoSearches(List.of(entity));
    }

    @Test
    void findById_shouldReturnMappedRequest_whenFound() {
        MongoAutoSearch entity = MongoAutoSearch.builder().id("id").build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().id("id").build();

        when(repository.findById("id")).thenReturn(Optional.of(entity));
        when(mapper.toAutoSearch(entity)).thenReturn(autoSearch);

        assertThat(queryAdapter.findById("id")).isEqualTo(autoSearch);

        verify(repository).findById("id");
        verify(mapper).toAutoSearch(entity);
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(repository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryAdapter.findById("missing-id"))
                .isInstanceOf(AutoSearchNotFoundException.class);

        verify(repository).findById("missing-id");
    }
}
