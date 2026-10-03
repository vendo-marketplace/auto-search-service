package com.vendo.auto_search_service.adapter.product.mapper;

import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.infrastructure.mapper.MapStructConfig;
import com.vendo.event_lib.auto_search.nested.AutoSearchProductEvent;
import com.vendo.event_lib.product.ProductCreatedEvent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface EventProductMapper {

    Product toProduct(ProductCreatedEvent event);

    List<AutoSearchProductEvent> toAutoSearchProductEvents(List<Product> products);

}
