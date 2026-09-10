package com.vendo.auto_search_service.adapter.product.mapper;

import com.vendo.auto_search_service.domain.product.Product;
import com.vendo.auto_search_service.infrastructure.mapper.MapStructConfig;
import com.vendo.event_lib.product.ProductCreatedEvent;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface EventProductMapper {

    // TODO cannot map address event
    Product toProduct(ProductCreatedEvent event);

}
