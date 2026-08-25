package com.vendo.auto_search_service.domain.auto_search;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AutoSearchTest {

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
