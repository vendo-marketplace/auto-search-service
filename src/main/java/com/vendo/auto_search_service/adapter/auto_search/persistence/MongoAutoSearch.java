package com.vendo.auto_search_service.adapter.auto_search.persistence;

import com.vendo.auto_search_service.domain.auto_search.SearchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class MongoAutoSearch {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String address;

    private SearchStatus status;

    private LocalDateTime expirationDate;
    private Set<String> notifiedProducts;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

}
