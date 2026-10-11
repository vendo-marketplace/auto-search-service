package com.vendo.auto_search_service.adapter.auto_search.out.persistence;

import com.vendo.auto_search_service.shared.Address;
import com.vendo.core_lib.constants.Separators;
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
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
final class MongoAutoSearchClient {

    private final MongoOperations op;

    private static final String minPriceField = ClassFields.nameOf("minPrice", MongoAutoSearch.class);
    private static final String maxPriceField = ClassFields.nameOf("maxPrice", MongoAutoSearch.class);
    private static final String addressField = ClassFields.nameOf("address", MongoAutoSearch.class);
    private static final String cityField = ClassFields.nameOf("city", Address.class);
    private static final String categoryIdField = ClassFields.nameOf("categoryId", MongoAutoSearch.class);

    Page<MongoAutoSearch> findAllBy(String categoryId, Address address, BigDecimal price, Pageable pageable) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new IllegalArgumentException("Category is required.");
        }

        List<Criteria> criteriaList = new ArrayList<>();

        criteriaList.add(Criteria.where(categoryIdField).is(categoryId));

        withAddressQuery(criteriaList, address);
        withPriceQuery(criteriaList, price);

        Query query = new Query(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        return withPageable(query, pageable);
    }

    private void withAddressQuery(List<Criteria> criteriaList, Address address) {
        if (ObjectUtils.isNotNull(address) && !StringUtils.isEmpty(address.city())) {

            String cityFormatted = addressField + Separators.DOT_SEPARATOR + cityField;

            Criteria criteria = new Criteria().orOperator(
                    Criteria.where(cityFormatted).is(address.city()),
                    Criteria.where(addressField).isNull()
            );

            criteriaList.add(criteria);
        }
    }

    private void withPriceQuery(List<Criteria> criteriaList, BigDecimal price) {
        if (ObjectUtils.isNotNull(price)) {

            Criteria minPriceCriteria = new Criteria().orOperator(
                    Criteria.where(minPriceField).lte(price),
                    Criteria.where(minPriceField).isNull()
            );

            Criteria maxPriceCriteria = new Criteria().orOperator(
                    Criteria.where(maxPriceField).gte(price),
                    Criteria.where(maxPriceField).isNull()
            );

            criteriaList.add(
                    new Criteria().andOperator(minPriceCriteria, maxPriceCriteria)
            );
        }
    }

    private Page<MongoAutoSearch> withPageable(Query query, Pageable pageable) {
        query.with(pageable);
        List<MongoAutoSearch> content = op.find(query, MongoAutoSearch.class);
        return new PageImpl<>(content, pageable, op.count(query, MongoAutoSearch.class));
    }
}