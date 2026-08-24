package com.vendo.auto_search_service.domain.auto_search;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AutoSearchTest {

    @Test
    void isOutdated_shouldReturnTrue_whenExpirationDateIsBeforeReferenceTime() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .expirationDate(LocalDateTime.now().minusDays(1))
                .build();

        assertThat(autoSearch.isOutdated(LocalDateTime.now())).isTrue();
    }

    @Test
    void isOutdated_shouldReturnFalse_whenExpirationDateIsAfterReferenceTime() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .expirationDate(LocalDateTime.now().plusDays(1))
                .build();

        assertThat(autoSearch.isOutdated(LocalDateTime.now())).isFalse();
    }

    @Test
    void expire_shouldSetStatusToExpired_andKeepOtherFieldsUnchanged() {
        AutoSearch autoSearch = AutoSearchDataBuilder.withAllFields()
                .status(SearchStatus.ACTIVE)
                .build();

        AutoSearch expired = autoSearch.expire();

        assertThat(expired.status()).isEqualTo(SearchStatus.EXPIRED);
        assertThat(expired.toBuilder().status(null).build())
                .isEqualTo(autoSearch.toBuilder().status(null).build());
    }
}
