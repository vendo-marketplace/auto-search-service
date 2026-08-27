package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.adapter.auto_search.out.mapper.AutoSearchMapper;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSearchCommandAdapterTest {

    @InjectMocks
    private AutoSearchCommandAdapter commandAdapter;

    @Mock
    private AutoSearchMapper mapper;
    @Mock
    private MongoAutoSearchRepository repository;

    @Test
    void save_shouldPersistMappedEntity() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().build();
        MongoAutoSearch entity = MongoAutoSearch.builder().id("id").build();

        when(mapper.toEntity(autoSearch)).thenReturn(entity);

        commandAdapter.save(autoSearch);

        verify(repository).save(entity);
    }

    @Test
    void update_shouldApplyChanges_whenRequestExists() {
        MongoAutoSearch entity = MongoAutoSearch.builder().id("id").build();
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields().id("id").build();

        when(repository.findById("id")).thenReturn(Optional.of(entity));

        commandAdapter.update("id", autoSearch);

        verify(mapper).updateEntity(entity, autoSearch);
        verify(repository).save(entity);
    }

    @Test
    void update_shouldThrow_whenRequestNotFound() {
        when(repository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandAdapter.update("missing-id", AutoSearchDataBuilder.withAllFields().build()))
                .isInstanceOf(AutoSearchNotFoundException.class);
    }

    @Test
    void delete_shouldRemoveRequest_whenItExists() {
        MongoAutoSearch entity = MongoAutoSearch.builder().id("id").build();

        when(repository.findById("id")).thenReturn(Optional.of(entity));

        commandAdapter.delete("id");

        verify(repository).delete(entity);
    }

    @Test
    void delete_shouldThrow_whenRequestNotFound() {
        when(repository.findById("missing-id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commandAdapter.delete("missing-id"))
                .isInstanceOf(AutoSearchNotFoundException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void expireOutdatedRequests_shouldUpdateActiveRequestsToExpired_andReturnUpdatedCount() {
        LocalDateTime referenceTime = LocalDateTime.now();
        when(repository.updateStatusForOutdatedRequests(eq(SearchStatus.ACTIVE), eq(referenceTime), eq(SearchStatus.EXPIRED), any()))
                .thenReturn(5L);

        long expiredCount = commandAdapter.expireOutdatedRequests(referenceTime);

        assertThat(expiredCount).isEqualTo(5L);
    }

    @Test
    void expireOutdatedRequests_shouldStampUpdatedAt() {
        LocalDateTime referenceTime = LocalDateTime.now();
        when(repository.updateStatusForOutdatedRequests(any(), any(), any(), any())).thenReturn(0L);

        commandAdapter.expireOutdatedRequests(referenceTime);

        ArgumentCaptor<Instant> updatedAtCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(repository).updateStatusForOutdatedRequests(eq(SearchStatus.ACTIVE), eq(referenceTime), eq(SearchStatus.EXPIRED), updatedAtCaptor.capture());
        assertThat(updatedAtCaptor.getValue()).isNotNull();
    }
}
