package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.domain.auto_search.nested.Owner;
import com.vendo.auto_search_service.domain.auto_search.type.SearchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
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
@CompoundIndex(name = "status_expiration_date_idx", def = "{'status': 1, 'expirationDate': 1}")
@CompoundIndexes({@CompoundIndex(name = "owner_id_idx", def = "{'owner.id': 1}"), @CompoundIndex(name = "owner_email_idx", def = "{'owner.email': 1}")})
public class MongoAutoSearch {

    @Id
    private String id;

    private Owner owner;

    private String categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String address;

    private SearchStatus status;

    private LocalDateTime expirationDate;
    private Set<String> products;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

}
