package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.core_lib.utils.ClassFields;
import com.vendo.core_lib.utils.ObjectUtils;
import com.vendo.core_lib.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
final class MongoAutoSearchClient {

    private final MongoOperations op;

    private static final String minPriceField = ClassFields.nameOf("minPrice", MongoAutoSearch.class);
    private static final String maxPriceField = ClassFields.nameOf("maxPrice", MongoAutoSearch.class);
    private static final String addressField = ClassFields.nameOf("address", MongoAutoSearch.class);
    private static final String categoryIdField = ClassFields.nameOf("categoryId", MongoAutoSearch.class);

    Page<MongoAutoSearch> findAllBy(String categoryId, String address, BigDecimal price, Pageable pageable) {
        if (StringUtils.isEmpty(categoryId)) throw new IllegalArgumentException("Category is required.");

        Query query = new Query(Criteria.where(categoryIdField).is(categoryId));

        withAddressQuery(query, address);
        withPriceQuery(query, price);

        return withPageable(query, pageable);
    }

    private void withAddressQuery(Query query, String address) {
        if (!StringUtils.isEmpty(address)) {
            Criteria criteria = Criteria.where(addressField)
                    .is(address)
                    .orOperator(Criteria.where(address).isNull());

            query.addCriteria(criteria);
        }
    }

    private void withPriceQuery(Query query, BigDecimal price) {
        if (ObjectUtils.isNotNull(price)) {
            Criteria minPriceCriteria = Criteria.where(minPriceField)
                    .lte(price)
                    .orOperator(Criteria.where(minPriceField).isNull());

            Criteria maxPriceCriteria = Criteria.where(maxPriceField)
                    .gte(price)
                    .orOperator(Criteria.where(maxPriceField).isNull());

            query.addCriteria(minPriceCriteria.andOperator(maxPriceCriteria));
        }
    }

    private Page<MongoAutoSearch> withPageable(Query query, Pageable pageable) {
        long total = op.count(query, MongoAutoSearch.class);
        query.with(pageable);
        List<MongoAutoSearch> content = op.find(query, MongoAutoSearch.class);
        return new PageImpl<>(content, pageable, total);
    }
}
